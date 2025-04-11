package com.example.demo.repository

import com.example.demo.model.Author
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository {
    fun findAll(): List<Author>
    fun findById(id: String): Author?
    fun save(author: Author): Author
    fun deleteById(id: String)
} 