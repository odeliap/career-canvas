package careercanvas.io.email

trait EmailService {

  def sendResetCode(to: String, resetCode: String): Unit

  def resetPasswordEmailHtml(resetCode: String): String =
    s"""
       |<html>
       |<head>
       |    <style>
       |        body {
       |            font-family: Arial, Helvetica, sans-serif;
       |            margin: 0;
       |            padding: 0;
       |            background-color: #f4f4f4;
       |        }
       |
       |        .container {
       |            max-width: 600px;
       |            margin: 0 auto;
       |            padding: 20px;
       |            background-color: #ffffff;
       |            box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.1);
       |            border-radius: 5px;
       |        }
       |
       |        h1 {
       |            color: #333333;
       |        }
       |
       |        p {
       |            color: #666666;
       |        }
       |
       |        .button {
       |            display: inline-block;
       |            padding: 10px 20px;
       |            background-color: #007bff;
       |            color: #ffffff;
       |            text-decoration: none;
       |            border-radius: 5px;
       |        }
       |
       |        .button:hover {
       |            background-color: #0056b3;
       |        }
       |    </style>
       |</head>
       |<body>
       |    <div class="container">
       |        <h1>Reset Your CareerCanvas Password</h1>
       |        <p>Enter this code to complete the reset:</p>
       |        <p style="font-weight: bold;">$resetCode</p>
       |        <p>If you didn't request this PIN, we recommend you change your CareerCanvas password.</p>
       |        <p>Go to Profile &gt; Edit Profile &gt; Change Password</p>
       |        <p>Thanks for helping us keep your account secure.</p>
       |        <p><em>The CareerCanvas Team</em></p>
       |    </div>
       |</body>
       |</html>""".stripMargin

}
