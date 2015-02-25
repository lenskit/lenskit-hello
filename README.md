# LensKit Demo Project

This is a demo project that shows how to create a project using [LensKit][] and set
get the recommender running.  It creates a simple command line application that
builds a recommender from a delimited text file of ratings, then recommends items
for users specified at the command line.

The main code is in `org.grouplens.lenskit.hello.HelloLenskit`. There are comments
so you can follow along and see what each stage of the process does.

If you are building a web application, you will need to adapt this project. But the
basic things done in that class will need to be done in your application somewhere.

The [LensKit home page][LensKit] has further documentation for LensKit, as well as
links to our bug tracker and wiki. Also be sure to subscribe to our [mailing list][]
and ask any further questions you may have about using LensKit, and follow our
[Twitter account][LensKitRS] for updates on new releases and developments.

## Project Setup

This project uses [Gradle][gradle] for build and dependency management. It is
easy to import into an IDE; Gradle support is included with or available for
NetBeans, IntelliJ IDEA, and Eclipse.  These IDEs will import your project directly
from the Gradle `build.gradle` file and set up the build and dependencies.

The `build.gradle` file contains the project definition and its dependencies. Review
this for how we pull in LensKit, and how to depend on other modules.

## Building and Running

In the Gradle build, we use the Application plugin to create a shell script and copy
the dependency JARs in order to run the LensKit application.

[ML100K]: https://github.com/grouplens/lenskit/wiki/ML100K

You'll also need a data set.  You can get the MovieLens 100K data set [here][ML100K].

Once you have a data set, you can run lenskit-hello through your IDE, or from the command line
as follows:

    $ ./gradlew build
    $ /bin/sh target/hello/bin/lenskit-hello.sh ml100k/u.data <userid>

The default delimiter is the tab character.

Have fun!

[LensKit]: http://lenskit.grouplens.org
[gradle]: http://gradle.org
[MercurialEclipse]: http://javaforge.com/project/HGE
[AppAssembler]: http://mojo.codehaus.org/appassembler/appassembler-maven-plugin/
[mailing list]: https://wwws.cs.umn.edu/mm-cs/listinfo/lenskit
[LensKitRS]: http://twitter.com/LensKitRS

## Other Versions

Various people have ported the lenskit-hello project to other languages:

- [Clojure](https://github.com/dcj/clj-lenskit-hello) ([original Gist](https://gist.github.com/llasram/6472144))
- [Python (Jython)](http://pastie.org/8298159)
- [Ruby (JRuby)](https://gist.github.com/joshjordan/6446324)
- [Scala](https://github.com/matt-thomson/lenskit-hello-scala/)
