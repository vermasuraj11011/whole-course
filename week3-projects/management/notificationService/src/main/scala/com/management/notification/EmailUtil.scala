package com.management.notification

import java.util.Properties
import jakarta.mail.{Address, Message, Session, Transport}
import jakarta.mail.internet.{InternetAddress, MimeMessage}

object EmailUtil {

  def sendEmail(to: String, subject: String, body: String): Unit = {
    val properties = new Properties()
    properties.put("mail.smtp.auth", "true")
    properties.put("mail.smtp.starttls.enable", "true")
    properties.put("mail.smtp.host", "smtp.google.com")
    properties.put("mail.smtp.port", "587")
    properties.put("mail.smtp.ssl.trust", "smtp.gmail.com")

    val session =
      Session.getInstance(
        properties,
        new jakarta.mail.Authenticator() {
          override protected def getPasswordAuthentication =
            new jakarta.mail.PasswordAuthentication("<EMAIL>", "<PASSWORD>")
        }
      )

    try {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress("<EMAIL>"))
      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to).asInstanceOf[Array[Address]])
      message.setSubject(subject)
      message.setText(body)

      Transport.send(message)
      println("Email sent successfully")
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }
}
