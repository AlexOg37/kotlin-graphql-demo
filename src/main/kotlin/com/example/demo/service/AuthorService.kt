package com.example.demo.service

import com.example.demo.model.Author
import com.example.demo.repository.AuthorRepository
import org.springframework.stereotype.Service

@Service
class AuthorService(private val authorRepository: AuthorRepository) {
    
    fun getAllAuthors(): List<Author> = authorRepository.findAll()
    
    fun getAuthorById(id: String): Author? = authorRepository.findById(id)
    
    fun createAuthor(author: Author): Author = authorRepository.save(author)
    
    fun updateAuthor(id: String, author: Author): Author? {
        return if (authorRepository.findById(id) != null) {
            authorRepository.save(author.copy(id = id))
        } else null
    }
    
    fun deleteAuthor(id: String) = authorRepository.deleteById(id)
} 