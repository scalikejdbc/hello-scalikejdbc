package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._

class ProgrammerSpec extends Specification with settings.DBSettings {

  val _p = Programmer.p

  trait AutoRollbackWithFixture extends AutoRollback {
    override def fixture(implicit session: DBSession) {
      applyUpdate(delete from ProgrammerSkill)
      applyUpdate(delete from Programmer)
      applyUpdate(delete from Skill)
      applyUpdate(delete from Company)

      val scala = Skill.create("Scala")
      val java = Skill.create("Java")
      val ruby = Skill.create("Ruby")

      val programmer = Programmer.create("seratch")
      programmer.addSkill(scala)
      programmer.addSkill(java)
      programmer.addSkill(ruby)

      val company = Company.create("M3")
      programmer.copy(companyId = Some(company.id)).save()

      Programmer.create("no_skill_programmer")

      val anon = Programmer.create("anonymous")
      anon.addSkill(java)

      Programmer.create("should_be_deteled").destroy()
    }
  }

  "Programmer" should {
    "find with skills" in new AutoRollbackWithFixture {
      val seratch = Programmer.findAllBy(sqls.eq(_p.name, "seratch")).head
      seratch.skills.size should_== (3)
    }
    "find no skill programmers" in new AutoRollbackWithFixture {
      val noSkillProgrammers = Programmer.findNoSkillProgrammers()
      noSkillProgrammers.size should_== (1)
    }
    "find by primary keys" in new AutoRollbackWithFixture {
      val id = Programmer.findAll().head.id
      val maybeFound = Programmer.find(id)
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollbackWithFixture {
      val allResults = Programmer.findAll()
      allResults.size should_== (3)
    }
    "count all records" in new AutoRollbackWithFixture {
      val count = Programmer.countAll()
      count should_== (3L)
    }
    "find by where clauses" in new AutoRollbackWithFixture {
      val results = Programmer.findAllBy(sqls.isNotNull(_p.companyId))
      results.head.name should_== ("seratch")
    }
    "count by where clauses" in new AutoRollbackWithFixture {
      val count = Programmer.countBy(sqls.isNull(_p.companyId))
      count should_== (2L)
    }
    "create new record" in new AutoRollbackWithFixture {
      val martin = Programmer.create("Martin")
      martin.id should not beNull
    }
    "save a record" in new AutoRollbackWithFixture {
      val entity = Programmer.findAll().head
      entity.copy(name = "Bob").save()
      val updated = Programmer.find(entity.id).get
      updated.name should_== ("Bob")
    }
    "destroy a record" in new AutoRollbackWithFixture {
      val entity = Programmer.findAll().head
      entity.destroy()
      val shouldBeNone = Programmer.find(entity.id)
      shouldBeNone.isDefined should beFalse
      Programmer.countAll should_== (2L)
    }
  }

}

