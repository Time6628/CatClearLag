## Building with Gradle

### 1. Cloning the repo
This guide assumes you have git installed, if not, use [this guide](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

First and foremost, you will need to clone the repo using the .git in the **Clone or Download** button.
The command you will run is likely:
```
git clone https://github.com/Time6628/CatClearLag.git
```

### 2. Building the jar
**Make sure you are using the JDK**

Go to the locally cloned repo, open your terminal or cmd and run ```gradlew build```
For most terminals other than Windows cmd, you will need to prefix it with ```./```
If you receive an error about missing a tools.jar, you are not using the JDK.

If everything was successful you should find the built jar file in `build/libs/`