package models

import scalikejdbc._
import org.joda.time.DateTime

case class Programmer(
    id: Long,
    name: String,
    companyId: Option[Long] = None,
    company: Option[Company] = None,
    skills: Seq[Skill] = Nil,
    createdAt: DateTime,
    deletedAt: Option[DateTime] = None) {

  def save()(implicit session: DBSession = Programmer.autoSession): Programmer = Programmer.save(this)(session)
  def destroy()(implicit session: DBSession = Programmer.autoSession): Unit = Programmer.destroy(id)(session)

  private val (ps, p, s) = (ProgrammerSkill.ps, Programmer.p, Skill.s)
  private val column = ProgrammerSkill.column

  def addSkill(skill: Skill)(implicit session: DBSession = Programmer.autoSession): Unit = withSQL {
    insert.into(ProgrammerSkill)
      .namedValues(column.programmerId -> id, column.skillId -> skill.id)
  }.update.apply()

  def deleteSkill(skill: Skill)(implicit session: DBSession = Programmer.autoSession): Unit = withSQL {
    delete.from(ProgrammerSkill)
      .where.eq(column.programmerId, id).and.eq(column.skillId, skill.id)
  }.update.apply()

}

object Programmer extends SQLSyntaxSupport[Programmer] {

  // If the table name is same as snake_case'd name of this companion object, you don't need to specify tableName explicitly.
  override val tableName = "programmer"
  // By default, column names will be cached from meta data automatically when accessing this table for the first time.
  override val columns = Seq("id", "name", "company_id", "created_timestamp", "deleted_timestamp")
  // If you need mapping between fields and columns, use nameConverters.
  override val nameConverters = Map("At$" -> "_timestamp")

  // simple extractor
  def apply(p: SyntaxProvider[Programmer])(rs: WrappedResultSet): Programmer = apply(p.resultName)(rs)
  def apply(p: ResultName[Programmer])(rs: WrappedResultSet): Programmer = new Programmer(
    id = rs.get(p.id),
    name = rs.get(p.name),
    companyId = rs.get(p.companyId),
    createdAt = rs.get(p.createdAt),
    deletedAt = rs.get(p.deletedAt)
  )

  // join query with company table
  def apply(p: SyntaxProvider[Programmer], c: SyntaxProvider[Company])(rs: WrappedResultSet): Programmer = {
    apply(p.resultName)(rs).copy(company = rs.longOpt(c.resultName.id).flatMap { _ =>
      if (rs.timestampOpt(c.resultName.deletedAt).isEmpty) Some(Company(c)(rs)) else None
    })
  }

  // SyntaxProvider objects
  val p = Programmer.syntax("p")

  private val (c, s, ps) = (Company.c, Skill.s, ProgrammerSkill.ps)

  // reusable part of SQL
  private val isNotDeleted = sqls.isNull(p.deletedAt)

  // find by primary key
  def find(id: Long)(implicit session: DBSession = autoSession): Option[Programmer] = withSQL {
    select
      .from(Programmer as p)
      .leftJoin(Company as c).on(p.companyId, c.id)
      .leftJoin(ProgrammerSkill as ps).on(ps.programmerId, p.id)
      .leftJoin(Skill as s).on(sqls.eq(ps.skillId, s.id).and.isNull(s.deletedAt))
      .where.eq(p.id, id).and.append(isNotDeleted)
  }.one(Programmer(p, c))
    .toMany(Skill.opt(s))
    .map { (programmer, skills) => programmer.copy(skills = skills) }
    .single.apply()

  // programmer with company(optional) with skills(many)
  def findAll()(implicit session: DBSession = autoSession): List[Programmer] = withSQL {
    select
      .from(Programmer as p)
      .leftJoin(Company as c).on(p.companyId, c.id)
      .leftJoin(ProgrammerSkill as ps).on(ps.programmerId, p.id)
      .leftJoin(Skill as s).on(sqls.eq(ps.skillId, s.id).and.isNull(s.deletedAt))
      .where.append(isNotDeleted)
      .orderBy(p.id)
  }.one(Programmer(p, c))
    .toMany(Skill.opt(s))
    .map { (programmer, skills) => programmer.copy(skills = skills) }
    .list.apply()

  def findNoSkillProgrammers()(implicit session: DBSession = autoSession): List[Programmer] = withSQL {
    select
      .from(Programmer as p)
      .leftJoin(Company as c).on(p.companyId, c.id)
      .where.notIn(p.id, select(sqls.distinct(ps.programmerId)).from(ProgrammerSkill as ps))
      .and.append(isNotDeleted)
      .orderBy(p.id)
  }.map(Programmer(p, c)).list.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Programmer as p).where.append(isNotDeleted)
  }.map(rs => rs.long(1)).single.apply().get

  def findAllBy(where: SQLSyntax, withCompany: Boolean = true)(implicit session: DBSession = autoSession): List[Programmer] = withSQL {
    select
      .from[Programmer](Programmer as p)
      .map(sql => if (withCompany) sql.leftJoin(Company as c).on(p.companyId, c.id) else sql) // dynamic
      .leftJoin(ProgrammerSkill as ps).on(ps.programmerId, p.id)
      .leftJoin(Skill as s).on(sqls.eq(ps.skillId, s.id).and.isNull(s.deletedAt))
      .where.append(isNotDeleted).and.append(sqls"${where}")
  }.one(rs => if (withCompany) Programmer(p, c)(rs) else Programmer(p)(rs))
    .toMany(Skill.opt(s))
    .map { (programmer, skills) => programmer.copy(skills = skills) }
    .list.apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Programmer as p).where.append(isNotDeleted).and.append(sqls"${where}")
  }.map(_.long(1)).single.apply().get

  def create(name: String, companyId: Option[Long] = None, createdAt: DateTime = DateTime.now)(implicit session: DBSession = autoSession): Programmer = {
    if (companyId.isDefined && Company.find(companyId.get).isEmpty) {
      throw new IllegalArgumentException(s"Company is not found! (companyId: ${companyId})")
    }
    val id = withSQL {
      insert.into(Programmer).namedValues(
        column.name -> name, 
        column.companyId -> companyId, 
        column.createdAt -> createdAt)
    }.updateAndReturnGeneratedKey.apply()

    Programmer(
      id = id,
      name = name,
      companyId = companyId,
      company = companyId.flatMap(id => Company.find(id)),
      createdAt = createdAt)
  }

  def save(m: Programmer)(implicit session: DBSession = autoSession): Programmer = {
    withSQL {
      update(Programmer).set(
        column.name -> m.name, 
        column.companyId -> m.companyId
      ).where.eq(column.id, m.id).and.isNull(column.deletedAt)
    }.update.apply()
    m
  }

  def destroy(id: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    update(Programmer).set(column.deletedAt -> DateTime.now).where.eq(column.id, id)
  }.update.apply()

}
