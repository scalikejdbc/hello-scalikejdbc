package models

import java.time.ZonedDateTime

import scalikejdbc._

case class Company(
  id: Long,
  name: String,
  url: Option[String] = None,
  createdAt: ZonedDateTime,
  deletedAt: Option[ZonedDateTime] = None) {

  def save()(implicit session: DBSession = Company.autoSession): Company = Company.save(this)(session)
  def destroy()(implicit session: DBSession = Company.autoSession): Unit = Company.destroy(id)(session)
}

object Company extends SQLSyntaxSupport[Company] {

  def apply(c: SyntaxProvider[Company])(rs: WrappedResultSet): Company = apply(c.resultName)(rs)
  def apply(c: ResultName[Company])(rs: WrappedResultSet): Company = new Company(
    id = rs.get(c.id),
    name = rs.get(c.name),
    url = rs.get(c.url),
    createdAt = rs.get(c.createdAt),
    deletedAt = rs.get(c.deletedAt))

  val c = Company.syntax("c")
  private val isNotDeleted = sqls.isNull(c.deletedAt)

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Company] = withSQL {
    select.from(Company as c).where.eq(c.id, id).and.append(isNotDeleted)
  }.map(Company(c)).single.apply()

  def findAll()(implicit session: DBSession = autoSession): List[Company] = withSQL {
    select.from(Company as c)
      .where.append(isNotDeleted)
      .orderBy(c.id)
  }.map(Company(c)).list.apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Company as c).where.append(isNotDeleted)
  }.map(rs => rs.long(1)).single.apply().get

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Company] = withSQL {
    select.from(Company as c)
      .where.append(isNotDeleted).and.append(sqls"${where}")
      .orderBy(c.id)
  }.map(Company(c)).list.apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls.count).from(Company as c).where.append(isNotDeleted).and.append(sqls"${where}")
  }.map(_.long(1)).single.apply().get

  def create(name: String, url: Option[String] = None, createdAt: ZonedDateTime = ZonedDateTime.now)(implicit session: DBSession = autoSession): Company = {
    val id = withSQL {
      insert.into(Company).namedValues(
        column.name -> name,
        column.url -> url,
        column.createdAt -> createdAt)
    }.updateAndReturnGeneratedKey.apply()

    Company(id = id, name = name, url = url, createdAt = createdAt)
  }

  def save(m: Company)(implicit session: DBSession = autoSession): Company = {
    withSQL {
      update(Company).set(
        column.name -> m.name,
        column.url -> m.url).where.eq(column.id, m.id).and.isNull(column.deletedAt)
    }.update.apply()
    m
  }

  def destroy(id: Long)(implicit session: DBSession = autoSession): Unit = withSQL {
    update(Company).set(column.deletedAt -> ZonedDateTime.now).where.eq(column.id, id)
  }.update.apply()

}
