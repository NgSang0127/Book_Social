package org.sang.booksocialnetwork.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//lớp record tạo các thành phần bâất biến(sau khi khoi tao doi tuong thi nhung gia tri nay khong thay doi duoc) va Tự
// động tạo getter, equals, hashCode và toString
public record BookRequest(
		Integer id,
		@NotNull(message = "100")
		@NotEmpty(message = "100")
		String title,
		@NotNull(message = "101")
		@NotEmpty(message = "101")
		String authorName,
		@NotNull(message = "102")
		@NotEmpty(message = "102")
		String isbn,
		@NotNull(message = "103")
		@NotEmpty(message = "103")
		String synopsis,
		boolean shareable) {

}
