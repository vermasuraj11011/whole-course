case class Employee(var sno: Int, var name: String, var city: String) {
  override def toString: String = s"Emp(sno: $sno, name: $name, city: $city)"
}

case class OrgNode(name: String, var parent: OrgNode, var children: List[OrgNode], var emp: List[Employee]) {
  override def toString: String = s"Org(name: ${name}, parent: ${parent.name}, children: ${children}, emp: $emp"
}

import scala.io.StdIn.readLine
import scala.util.matching.Regex


object Task5 {

  var root: OrgNode = OrgNode("Apple", null, List.empty[OrgNode], null)

  private def printOrg(node: OrgNode, prefix: String = "", isLast: Boolean = true, isRoot: Boolean = true): Unit = {
    if (isRoot) {
      println(node.name)
    } else {
      val connector = if (isLast) "└── " else "├── "
      println(s"$prefix$connector${node.name}")
    }

    val childPrefix = if (isLast) prefix + "    " else prefix + "│   "

    if (node.emp != null && node.emp.nonEmpty) {
      node.emp.zipWithIndex.foreach { case (emp, empIndex) =>
        val empConnector = if (empIndex == node.emp.length - 1 && node.children.isEmpty) "└── " else "├── "
        println(s"$childPrefix$empConnector(${emp.sno}, ${emp.name}, ${emp.city})")
      }
    }

    if (node.children != null && node.children.nonEmpty) {
      node.children.zipWithIndex.foreach { case (child, index) =>
        val lastChild = index == node.children.length - 1
        printOrg(child, childPrefix, lastChild, isRoot = false)
      }
    }
  }


  private def findTheNode(node: OrgNode, cur: String): OrgNode = {
    val head = node
    if (head.name.equals(cur)) {
      head
    } else {
      if (head.children != null && head.children.nonEmpty) {
        for (child <- head.children) {
          val result: OrgNode = findTheNode(child, cur)
          if (result != null) {
            return result
          }
        }
        null
      } else {
        null
      }
    }
  }

  private def prompts(): Unit = {
    println("Press <p> to see the organization structure")
    println("Press <e> exit the application")
    println("For enter employee details, pass data in the below format")
    println("<parent_org>,<current_org>,(<sno>,<name>,<city>)")
  }

  def main(args: Array[String]): Unit = {
    println("Application is started")
    prompts()
    val exit = "e"
    var input = readLine()

    while (!input.equals(exit)) {
      if (input.equals("p")) {
        printOrg(root)
      } else {
        val pattern: Regex = """([^,]+),([^,]+),\((\d+),([^,]+),([^,]+)\)""".r

        input match {
          case pattern(parentOrg, currentOrg, sno, name, city) =>
            var newEmployee = Employee(sno = sno.toInt, name = name, city = city)
            var parentNode = findTheNode(node = root, cur = parentOrg)
            if (parentNode != null) {
              var childNode = findTheNode(node = parentNode, cur = currentOrg)
              if (childNode != null) {
                childNode.emp = childNode.emp :+ newEmployee
              } else {
                val newChildNode = OrgNode(currentOrg, parentNode, List.empty[OrgNode], List(newEmployee))
                parentNode.children = parentNode.children :+ newChildNode
              }
            } else {
              parentNode = OrgNode(name = parentOrg, parent = root, children = List.empty[OrgNode], emp = null)
              val newChildNode = OrgNode(currentOrg, parentNode, List.empty[OrgNode], List(newEmployee))
              parentNode.children = parentNode.children :+ newChildNode
              root.children = root.children :+ parentNode
            }
          case _ =>
            println("Input string does not match the expected format.")
        }
      }

      prompts()
      input = readLine()
    }

    println("Application is closed successfully.....")
  }
}

// output

//    Application is started
//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    p
//    Apple

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Hr,Salary,(1, John, Delhi)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Hr,Salary,(2,Rohan,Mumbai)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    p
//    Apple
//        └── Hr
//            └── Salary
//                ├── (1,  John,  Delhi)
//                └── (2, Rohan, Mumbai)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Iphone,Iphone-15,(3,Mohan,Indore)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Iphone,Iphone-16,(4,Chintu,Indore)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Mac,Mac-16inch,(5,Sohan,Mumbai)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Mac,Mac-16inch,(6,Sameer,Delhi)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Mac,Mac-14inch,(7,Rakesh,Bengaluru)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Maps,Backend,(8,Suraj,Hyderabad)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Maps,Backend,(9,Aman,Hyderabad)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Maps,Backend,(10,Deva,Hyderabad)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    Maps,Frontend,(11,Rohit,Chennai)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    p
//    Apple
//        ├── Hr
//        │   └── Salary
//        │       ├── (1,  John,  Delhi)
//        │       └── (2, Rohan, Mumbai)
//        ├── Iphone
//        │   ├── Iphone-15
//        │   │   └── (3, Mohan, Indore)
//        │   └── Iphone-16
//        │       └── (4, Chintu, Indore)
//        ├── Mac
//        │   ├── Mac-16inch
//        │   │   ├── (5, Sohan, Mumbai)
//        │   │   └── (6, Sameer, Delhi)
//        │   └── Mac-14inch
//        │       └── (7, Rakesh, Bengaluru)
//        └── Maps
//            ├── Backend
//            │   ├── (8, Suraj, Hyderabad)
//            │   ├── (9, Aman, Hyderabad)
//            │   └── (10, Deva, Hyderabad)
//            └── Frontend
//                └── (11, Rohit, Chennai)

//    Press <p> to see the organization structure
//    Press <e> exit the application
//    For enter employee details, pass data in the below format
//    <parent_org>,<current_org>,(<sno>,<name>,<city>)

//    e
//
//    Process finished with exit code 0