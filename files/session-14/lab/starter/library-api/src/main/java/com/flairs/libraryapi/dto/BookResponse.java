package com.flairs.libraryapi.dto;

// TODO 2: the cache stores this in Redis with Java serialization. What must the record implement?
public record BookResponse(Long id, String title, String isbn, int year, int copies, Long authorId, String authorName) {
}
