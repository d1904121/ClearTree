# Tree Installation

1. import Tree Module `CheckableTreeView/checkabletreeview`

2. copy and paste `publish.gradle` to `Project/`

3. edit Gradle `Scripts/Project.build.gradle`:

```gradle
ext.dokka_version = '0.9.18'

classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4'
classpath "org.jetbrains.dokka:dokka-android-gradle-plugin:$dokka_version"

maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
```

4. edit Gradle `Scripts/app.build.gradle`:

```gradle
implementation project(':checkabletreeview')
```

5. click `Sync now`(or `Try Again`) and `checkabletreeview` library will appear

6. change the minSdkVersion of `checkabletreeview

7. click `Sync now`(or `Try Again`) to 19 (the origin is 23) by editing `Scripts/checkabletreeview.build.gradle`

8. update all modules using `File-Project Structure-Suggestions`

9. add `RecyclerView` module and add this to an activity:

```xml
    <com.f3401pal.checkabletreeview.SingleRecyclerViewImpl
        android:id="@+id/treeView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
```

10. add this to .kt:

```kotlin
private lateinit var treeView: SingleRecyclerViewImpl<StringNode>
//onCreate
treeView = findViewById<SingleRecyclerViewImpl<StringNode>>(R.id.treeView)
val root=TreeNodeFactory.buildTestTree()
treeView.setRoots(listOf(root))
```

11. test run and confirm that you can see a test tree(root-left-level3left-...)

# What I did

1. changed all List to MutableList:[What is the difference?](https://stackoverflow.com/questions/46445909/difference-between-mutablelist-and-list-in-kotlin)

>You can modify a MutableList: change, remove, add... its elements. In a List you can only read them.

so you can add or remove nodes. but I **recommend** you to updateRawTreeInstance->generateViewTree->treeView.setRoots

2. changed StringNode to general node

# Installation using this project

1. maybe just copy all files, i mean, TODO
