package models

import scalikejdbc._, SQLInterpolation._
import org.joda.time.DateTime

case class Skill(
    id: Long,
    name: String,
    createdAt: DateTime,
    deletedAt: Option[DateTime] = None) {

  def save()(implicit session: DBSession = Skill.autoSession): Skill = Skill.save(this)(session)
  def destroy()(implicit session: DBSession = Skill.autoSession): Unit = Skill.destroy(id)(session)
}

object Skill extends SQLSyntaxSupport[Skill] {

  def apply(s: SyntaxProvider[Skill])(rs: WrappedResultSet): Skill = apply(s.resultName)(rs)
  def apply(s: ResultName[Skill])(rs: WrappedResultSet): Skill = new Skill(
    id = rs.long(s.id),
    name = rs.string(s.name),
    createdAt = rs.timestamp(s.createdAt).toDateTime,
    deletedAt = rs.timestampOpt(s.deletedAt).map(_.toDateTime)
  )

  def opt(s: SyntaxProvider[Skill])(rs: WrappedResultSet): Option[Skill] = rs.longOpt(s.resultName.id).map(_ => apply(s.resultName)(rs))

  val s = Skill.syntax("s")

  private val isNotDeleted = sqls.isNull(s.deletedAt)

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Skill] = withSQL {
    select.from(Skill as s).where.eq(s.id, id).and.append(isNotDeleted)
  }.map(Skill(s)).single.apply()

  def findAll()(implicit session: DBSession = autoSession): List[Skill] = withSQL {
    select.from(Skill as s)
      .where.append(isNotDeleted)
      .orderBy(s.id)
  }.map(Skill(s)).list.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Skill as s).where.append(isNotDeleted)
  }.map(rs => rs.long(1)).single.apply().get

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Skill] = withSQL {
    select.from(Skill as s)
      .where.append(isNotDeleted).and.append(sqls"${where}")
      .orderBy(s.id)
  }.map(Skill(s)).list.apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Skill as s).where.append(isNotDeleted).and.append(sqls"${where}")
  }.map(_.long(1)).single.apply().get

  def create(name: String, createdAt: DateTime = DateTime.now)(implicit session: DBSession = autoSession): Skill = {
    val id = withSQL {
      insert.into(Skill).namedValues(column.name -> name, column.createdAt -> createdAt)
    }.updateAndReturnGeneratedKey.apply()

    Skill(id = id, name = name, createdAt = createdAt)
  }

  def save(m: Skill)(implicit session: DBSession = autoSession): Skill = {
    withSQL {
      update(Skill).set(column.name -> m.name).where.eq(column.id, m.id).and.isNull(column.deletedAt)
    }.update.apply()
    m
  }

  def destroy(id: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    update(Skill).set(column.deletedAt -> DateTime.now).where.eq(column.id, id)
  }.update.apply()

}
