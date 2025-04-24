package com.example.demo.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.util.matcher.RequestMatcher
import jakarta.servlet.ServletInputStream
import jakarta.servlet.ReadListener
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
open class SecurityConfig {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .addFilterBefore(CachedBodyFilter(), BasicAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/graphiql", "/graphiql/**").permitAll()
                    .requestMatchers(isIntrospectionQuery()).permitAll()
                    .requestMatchers("/graphql").authenticated()
                    .anyRequest().authenticated()
            }
            .httpBasic { basic ->
                basic.authenticationEntryPoint { request, response, authException ->
                    if (request.requestURI.startsWith("/graphql")) {
                        response.status = 401
                        response.contentType = "application/json"
                        response.addHeader("WWW-Authenticate", "Basic realm=\"GraphQL\"")
                        response.writer.write("""
                            {
                                "errors": [
                                    {
                                        "message": "Unauthorized",
                                        "extensions": {
                                            "code": "UNAUTHENTICATED"
                                        }
                                    }
                                ]
                            }
                        """.trimIndent())
                    } else {
                        response.sendError(403, "Forbidden")
                    }
                }
            }
        return http.build()
    }

    private fun isIntrospectionQuery(): RequestMatcher {
        return RequestMatcher { request ->
            if (request.method != "POST" || !request.requestURI.startsWith("/graphql")) {
                return@RequestMatcher false
            }

            try {
                val json = ObjectMapper().readTree(request.inputStream)
                val query = json.get("query")?.asText() ?: return@RequestMatcher false
                
                // Check if the query contains introspection fields
                query.contains("__schema") || query.contains("__type")
            } catch (e: Exception) {
                false
            }
        }
    }

    @Bean
    open fun userDetailsService(): UserDetailsService {
        val admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin"))
            .authorities("ROLE_ADMIN", "ROLE_BOOK_MANAGER", "ROLE_AUTHOR_MANAGER")
            .build()

        val bookManager = User.builder()
            .username("bookManager")
            .password(passwordEncoder().encode("book"))
            .authorities("ROLE_BOOK_MANAGER")
            .build()

        val authorManager = User.builder()
            .username("authorManager")
            .password(passwordEncoder().encode("author"))
            .authorities("ROLE_AUTHOR_MANAGER")
            .build()

        return InMemoryUserDetailsManager(admin, bookManager, authorManager)
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}

class CachedBodyFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            chain.doFilter(CachedBodyHttpServletRequest(request), response)
        } else {
            chain.doFilter(request, response)
        }
    }
}

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var cachedBody: ByteArray? = null

    init {
        try {
            val inputStream = request.inputStream
            cachedBody = inputStream.readAllBytes()
        } catch (e: IOException) {
            cachedBody = ByteArray(0)
        }
    }

    override fun getInputStream(): ServletInputStream {
        return object : ServletInputStream() {
            private val inputStream = ByteArrayInputStream(cachedBody!!)

            override fun read(): Int = inputStream.read()
            override fun read(b: ByteArray, off: Int, len: Int): Int = inputStream.read(b, off, len)
            override fun isFinished(): Boolean = inputStream.available() == 0
            override fun isReady(): Boolean = true
            override fun setReadListener(listener: ReadListener) {
                // No-op since we don't support async reading
            }
        }
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    }
} 