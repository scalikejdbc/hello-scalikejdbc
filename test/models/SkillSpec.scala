package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._

class SkillSpec extends Specification with settings.DBSettings {

  trait AutoRollbackWithFixture extends AutoRollback {
    override def fixture(implicit session: DBSession) {
      applyUpdate(delete from Skill)
      Skill.create("Scala")
      Skill.create("Java")
      Skill.create("Ruby")
      Skill.create("Perl")
      Skill.create("Python")
    }
  }

  "Skill" should {
    "find by primary keys" in new AutoRollbackWithFixture {
      val id = Skill.findAll().head.id
      val maybeFound = Skill.find(id)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollbackWithFixture {
      val allResults = Skill.findAll()
      allResults.size should_== (5)
    }
    "count all records" in new AutoRollbackWithFixture {
      val count = Skill.countAll()
      count should_== (5L)
    }
    "find by where clauses" in new AutoRollbackWithFixture {
      val results = Skill.findAllBy(sqls.in(Skill.column.name, Seq("Python", "Perl")))
      results.size should_== (2)
    }
    "count by where clauses" in new AutoRollbackWithFixture {
      val count = Skill.countBy(sqls"${Skill.column.name} like ${"P%"}")
      count should_== (2L)
    }
    "create new record" in new AutoRollbackWithFixture {
      val newSkill = Skill.create(name = "ScalikeJDBC")
      newSkill.id should not beNull
    }
    "save a record" in new AutoRollbackWithFixture {
      val entity = Skill.findAll().head
      entity.copy(name = "Erlang").save()
      val updated = Skill.find(entity.id).get
      updated.name should_== ("Erlang")
    }
    "destroy a record" in new AutoRollbackWithFixture {
      val entity = Skill.findAll().head
      entity.destroy()
      val shouldBeNone = Skill.find(entity.id)
      shouldBeNone.isDefined should beFalse
      Skill.countAll() should_== (4L)
    }
  }

}

