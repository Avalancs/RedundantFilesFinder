# Redundant Files Finder
A small utility to find redundant files. The program will prompt 3 questions: compare by file name, size and content.

For content comparison you have to answer `yes` to size comparison. Based on the answers you can get 5 different matching methods:
<table>
    <thead>
        <tr>
            <th>File Name</th>
            <th>Size</th>
            <th>Content</th>
            <th>kind of matching</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>+</td>
            <td>-</td>
            <td>&nbsp;</td>
            <td>Files with matching file name will be listed</td>
        </tr>
        <tr>
            <td>+</td>
            <td>+</td>
            <td>-</td>
            <td>Files with matching file name and sizes will be listed, but the contents will not be compared</td>
        </tr>
        <tr>
            <td>+</td>
            <td>+</td>
            <td>+</td>
            <td>Files with matching file name, size and content will be listed. Content comparing can be slow on folders with many or huge files!</td>
        </tr>
        <tr>
            <td>-</td>
            <td>+</td>
            <td>-</td>
            <td>Files with matching sizes will be listed.</td>
        </tr>
        <tr>
            <td>-</td>
            <td>+</td>
            <td>+</td>
            <td>Files with matching size and content will be listed. Content comparing can be slow on folders with many or huge files!</td>
        </tr>
    </tbody>
</table>

You need to run the jar file from the terminal, since the output is logged there. In a future version logging to a file will be implemented!

### Running from source
You need to use the `run` task of Gradle. If you do not have experience with Gradle, then follow these steps:

1. From the explorer
   1. open up the root folder of this project (where the build.gradle file is) in a command-line terminal
   2. type `gradlew run` or `./gradlew run` if you're on linux (you might also need to give permission with `chmod +x gradlew` first).
      - on first run it will download Gradle in your user directory, so it can run from there
      - it will also download dependencies, for example `MSLinks` then compile the program and run it
      - subsequent runs will be faster, as you will already have Gradle and the dependencies

2. From IntelliJ
   1. Import the project
   2. If a popup appears with `unimported Gradle project` then click on import, check `auto-import`
   3. In the Gradle window open `Tasks > application` and right click on `run` and select `Run RedundantFilesFinder`
      - accessible from `View -> Tool Windows -> Gradle` if the Gradle plugin is enabled in Idea

### Distribution

You can use the `distZip` task of Gradle. Follow the steps of `Running from source` section, 
but replace `run` with `distZip`. You will find the finished zip file in the `build/distributions` folder.

### Development

`IntelliJ Idea` with `Gradle`

### License

This project is under the MIT License. See `license.txt` for more details

### Contributors

AvaLanCS

### Version Information

@VERSION@_@COMMIT_REF@
