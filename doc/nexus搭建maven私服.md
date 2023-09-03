# 安装nexus
以下是使用Docker安装Nexus的步骤：
1. 在Docker Hub上搜索Nexus，并选择要安装的版本。
2. 使用以下命令从Docker Hub下载Nexus映像：
   ```
   docker pull sonatype/nexus3
   ```
3. 创建一个新的Docker容器并运行Nexus：
   ```
   docker run -d -p 8081:8081 --name nexus sonatype/nexus3
   ```
   这将创建一个名为“nexus”的新容器，并将Nexus运行在端口8081上。
4. 等待Nexus启动完毕，然后在浏览器中访问：
   ```
   http://localhost:8081
   ```
   这将打开Nexus的Web界面。
5. 在Nexus的Web界面中，您可以使用默认的管理员帐户登录。首次登录时，您将被要求更改默认密码。
6. 现在，您可以开始使用Nexus来管理和发布您的软件包。您可以将您的Maven，Gradle或其他构建工具配置为将软件包上传到Nexus，以便其他开发人员可以方便地访问和使用它们。