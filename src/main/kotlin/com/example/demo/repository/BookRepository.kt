package com.example.demo.repository

import com.example.demo.model.Book
import org.springframework.stereotype.Repository

@Repository
interface BookRepository {
    fun findAll(): List<Book>
    fun findById(id: String): Book?
    fun save(book: Book): Book
    fun deleteById(id: String)
} 