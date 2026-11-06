package com.flairs.libraryapi.dto;

// TODO 1: add Bean Validation annotations (with a message for each) so that:
//   title  : not blank, at most 120 characters
//   author : not blank
//   isbn   : not blank, exactly 13 digits  (@Pattern(regexp = "\\d{13}"))
//   year   : between 1450 and 2100         (@Min and @Max)
//   copies : zero or more                  (@PositiveOrZero)
public record CreateBookRequest(String title, String author, String isbn, int year, int copies) {
}
