package com.flairs.libraryapi.dto;

public record BookResponse(Long id, String title, String author, String isbn, int year, int copies) {
}
