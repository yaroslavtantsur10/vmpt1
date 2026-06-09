package com.example.laba4

import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var textOutput: TextView
    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPost: EditText
    private lateinit var editComment: EditText
    private lateinit var spinnerUser: Spinner
    private lateinit var spinnerCommentUser: Spinner
    private lateinit var spinnerCommentPost: Spinner

    private val userNames = mutableListOf<String>()
    private val userIds = mutableListOf<Int>()
    private val postTitles = mutableListOf<String>()
    private val postIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        textOutput         = findViewById(R.id.textOutput)
        editUsername       = findViewById(R.id.editUsername)
        editEmail          = findViewById(R.id.editEmail)
        editPost           = findViewById(R.id.editPost)
        editComment        = findViewById(R.id.editComment)
        spinnerUser        = findViewById(R.id.spinnerUser)
        spinnerCommentUser = findViewById(R.id.spinnerCommentUser)
        spinnerCommentPost = findViewById(R.id.spinnerCommentPost)

        loadUsersToSpinner()
        loadPostsToSpinner()

        findViewById<Button>(R.id.btnAddUser).setOnClickListener { addUser() }
        findViewById<Button>(R.id.btnAddPost).setOnClickListener { addPost() }
        findViewById<Button>(R.id.btnAddComment).setOnClickListener { addComment() }
        findViewById<Button>(R.id.btnShowUsers).setOnClickListener { showUsers() }
        findViewById<Button>(R.id.btnShowPosts).setOnClickListener { showPosts() }
        findViewById<Button>(R.id.btnShowComments).setOnClickListener { showComments() }
        findViewById<Button>(R.id.btnShowStats).setOnClickListener { showStats() }

        showUsers()
    }

    private fun loadUsersToSpinner() {
        userNames.clear()
        userIds.clear()
        val cursor = db.readableDatabase.rawQuery(
            "SELECT ${DatabaseHelper.COL_USER_ID}, ${DatabaseHelper.COL_USER_NAME} FROM ${DatabaseHelper.TABLE_USERS}",
            null
        )
        while (cursor.moveToNext()) {
            userIds.add(cursor.getInt(0))
            userNames.add(cursor.getString(1))
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUser.adapter = adapter

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCommentUser.adapter = adapter2
    }

    private fun loadPostsToSpinner() {
        postTitles.clear()
        postIds.clear()
        val cursor = db.readableDatabase.rawQuery(
            """SELECT p.${DatabaseHelper.COL_POST_ID}, u.${DatabaseHelper.COL_USER_NAME}, p.${DatabaseHelper.COL_POST_CONTENT}
               FROM ${DatabaseHelper.TABLE_POSTS} p
               JOIN ${DatabaseHelper.TABLE_USERS} u ON p.${DatabaseHelper.COL_POST_USER_ID} = u.${DatabaseHelper.COL_USER_ID}""",
            null
        )
        while (cursor.moveToNext()) {
            postIds.add(cursor.getInt(0))
            postTitles.add("${cursor.getString(1)}: ${cursor.getString(2).take(20)}")
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, postTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCommentPost.adapter = adapter
    }

    private fun addUser() {
        val name  = editUsername.text.toString().trim()
        val email = editEmail.text.toString().trim()
        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Введіть ім'я та email", Toast.LENGTH_SHORT).show()
            return
        }
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_NAME, name)
            put(DatabaseHelper.COL_EMAIL, email)
            put(DatabaseHelper.COL_PASSWORD, "hash_$name")
        }
        val id = db.writableDatabase.insert(DatabaseHelper.TABLE_USERS, null, values)
        if (id == -1L) {
            Toast.makeText(this, "Користувач вже існує!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Користувача додано (id=$id)", Toast.LENGTH_SHORT).show()
            editUsername.text.clear()
            editEmail.text.clear()
            loadUsersToSpinner()
            showUsers()
        }
    }

    private fun addPost() {
        val content = editPost.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(this, "Введіть текст поста", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedUserId = userIds[spinnerUser.selectedItemPosition]
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_POST_USER_ID, selectedUserId)
            put(DatabaseHelper.COL_POST_CONTENT, content)
        }
        val id = db.writableDatabase.insert(DatabaseHelper.TABLE_POSTS, null, values)
        Toast.makeText(this, "Пост додано (id=$id)", Toast.LENGTH_SHORT).show()
        editPost.text.clear()
        loadPostsToSpinner()
        showPosts()
    }

    private fun addComment() {
        val text = editComment.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, "Введіть текст коментаря", Toast.LENGTH_SHORT).show()
            return
        }
        if (postIds.isEmpty()) {
            Toast.makeText(this, "Спочатку додайте пост!", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedUserId = userIds[spinnerCommentUser.selectedItemPosition]
        val selectedPostId = postIds[spinnerCommentPost.selectedItemPosition]

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_COMMENT_POST_ID, selectedPostId)
            put(DatabaseHelper.COL_COMMENT_USER_ID, selectedUserId)
            put(DatabaseHelper.COL_COMMENT_TEXT, text)
        }
        val id = db.writableDatabase.insert(DatabaseHelper.TABLE_COMMENTS, null, values)
        Toast.makeText(this, "Коментар додано (id=$id)", Toast.LENGTH_SHORT).show()
        editComment.text.clear()
        showComments()
    }

    private fun showUsers() {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT ${DatabaseHelper.COL_USER_ID}, ${DatabaseHelper.COL_USER_NAME}, ${DatabaseHelper.COL_EMAIL} FROM ${DatabaseHelper.TABLE_USERS}",
            null
        )
        val sb = StringBuilder("=== КОРИСТУВАЧІ ===\n")
        while (cursor.moveToNext()) {
            sb.append("ID: ${cursor.getInt(0)} | ${cursor.getString(1)} | ${cursor.getString(2)}\n")
        }
        cursor.close()
        textOutput.text = sb.toString()
    }

    private fun showPosts() {
        val cursor = db.readableDatabase.rawQuery(
            """SELECT p.${DatabaseHelper.COL_POST_ID}, u.${DatabaseHelper.COL_USER_NAME}, p.${DatabaseHelper.COL_POST_CONTENT}
               FROM ${DatabaseHelper.TABLE_POSTS} p
               JOIN ${DatabaseHelper.TABLE_USERS} u ON p.${DatabaseHelper.COL_POST_USER_ID} = u.${DatabaseHelper.COL_USER_ID}""",
            null
        )
        val sb = StringBuilder("=== ПОСТИ ===\n")
        while (cursor.moveToNext()) {
            sb.append("ID: ${cursor.getInt(0)} | ${cursor.getString(1)}: ${cursor.getString(2)}\n")
        }
        cursor.close()
        textOutput.text = sb.toString()
    }

    private fun showComments() {
        val cursor = db.readableDatabase.rawQuery(
            """SELECT c.${DatabaseHelper.COL_COMMENT_ID},
                      u.${DatabaseHelper.COL_USER_NAME},
                      p.${DatabaseHelper.COL_POST_CONTENT},
                      c.${DatabaseHelper.COL_COMMENT_TEXT}
               FROM ${DatabaseHelper.TABLE_COMMENTS} c
               JOIN ${DatabaseHelper.TABLE_USERS} u ON c.${DatabaseHelper.COL_COMMENT_USER_ID} = u.${DatabaseHelper.COL_USER_ID}
               JOIN ${DatabaseHelper.TABLE_POSTS} p ON c.${DatabaseHelper.COL_COMMENT_POST_ID} = p.${DatabaseHelper.COL_POST_ID}""",
            null
        )
        val sb = StringBuilder("=== КОМЕНТАРІ ===\n")
        while (cursor.moveToNext()) {
            sb.append("ID: ${cursor.getInt(0)} | ${cursor.getString(1)} → \"${cursor.getString(2).take(15)}...\": ${cursor.getString(3)}\n")
        }
        cursor.close()
        textOutput.text = sb.toString()
    }

    private fun showStats() {
        val dbRead = db.readableDatabase
        val users    = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS}", null)
        val posts    = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_POSTS}", null)
        val comments = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_COMMENTS}", null)
        val likes    = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_LIKES}", null)
        val friends  = dbRead.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_FRIENDS}", null)

        users.moveToFirst(); posts.moveToFirst()
        comments.moveToFirst(); likes.moveToFirst(); friends.moveToFirst()

        textOutput.text = """
            === СТАТИСТИКА ===
            Користувачів: ${users.getInt(0)}
            Постів: ${posts.getInt(0)}
            Коментарів: ${comments.getInt(0)}
            Лайків: ${likes.getInt(0)}
            Зв'язків друзів: ${friends.getInt(0)}
        """.trimIndent()

        users.close(); posts.close(); comments.close(); likes.close(); friends.close()
    }
}