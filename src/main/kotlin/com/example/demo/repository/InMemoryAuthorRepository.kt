package com.example.demo.repository

import com.example.demo.model.Author
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryAuthorRepository : AuthorRepository {
    private val authors = ConcurrentHashMap<String, Author>()

    init {
        // Initialize with some sample data
        save(Author("1", "J.R.R.", "Tolkien"))
        save(Author("2", "George", "Orwell"))
    }

    override fun findAll(): List<Author> = authors.values.toList()

    override fun findById(id: String): Author? = authors[id]

    override fun save(author: Author): Author {
        authors[author.id] = author
        return author
    }

    override fun deleteById(id: String) {
        authors.remove(id)
    }
} 