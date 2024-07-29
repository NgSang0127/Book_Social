package org.sang.booksocialnetwork.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sang.booksocialnetwork.common.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

	private final BookService bookService;

	@PostMapping
	public ResponseEntity<Integer> saveBook(
			@RequestBody @Valid BookRequest request,
			Authentication connectedUser
	) {
		return ResponseEntity.ok(bookService.save(request, connectedUser));
	}

	@GetMapping("/{book-id}")
	public ResponseEntity<BookResponse> findBookById(
			@PathVariable("book-id") Integer bookId
	) {
		return ResponseEntity.ok(bookService.findById(bookId));
	}

	;

	@GetMapping
	public ResponseEntity<PageResponse<BookResponse>> findAllBooks(//tìm những sách mà người dùng chưa mượn
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser
	) {
		return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));

	}

	;

	@GetMapping("/owner")
	public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser
	) {
		return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));

	}

	;

	@GetMapping("/borrowed")
	public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBook(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser
	) {
		return ResponseEntity.ok(bookService.findAllBorrowedBooks(page, size, connectedUser));

	}

	;

	//tìm sách trả lại cho chủ nhân của nó
	@GetMapping("/returned")
	public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBook(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size,
			Authentication connectedUser
	) {
		return ResponseEntity.ok(bookService.findAllReturnedBooks(page, size, connectedUser));

	}


	/*
	@PutMapping: Dùng để cập nhật toàn bộ tài nguyên. Yêu cầu cung cấp tất cả các thuộc tính của tài nguyên.
    @PatchMapping: Dùng để cập nhật một phần của tài nguyên. Yêu cầu chỉ cung cấp các thuộc tính cần cập nhật.
	 */
	@PatchMapping("/shareable/{book-id}")
	public ResponseEntity<Integer> updateShareableStatus(
			@PathVariable("book-id") Integer bookId,
			Authentication connectedUser
	){
		return ResponseEntity.ok(bookService.updateShareableStatus(bookId,connectedUser));
	};

	@PatchMapping("/archived/{book-id}")
	public ResponseEntity<Integer> updateArchivedStatus(
			@PathVariable("book-id") Integer bookId,
			Authentication connectedUser
	){
		return ResponseEntity.ok(bookService.updateArchivedStatus(bookId,connectedUser));
	};

	@PostMapping("/borrow/{book-id}")
	public ResponseEntity<Integer>borrowBook(
			@PathVariable("book-id") Integer bookId,
			Authentication connectedUser
	){
		return ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));
	}

	@PatchMapping("/borrow/return/{book-id}")
	public ResponseEntity<Integer> returnBorrowBook(
			@PathVariable("book-id") Integer bookId,
			Authentication connectedUser
	){
		return ResponseEntity.ok(bookService.returnBorrowedBook(bookId,connectedUser));
	}

	@PatchMapping("/borrow/return/approve/{book-id}")
	public ResponseEntity<Integer> returnApproveBorrowBook(
			@PathVariable("book-id") Integer bookId,
			Authentication connectedUser
	){
		return ResponseEntity.ok(bookService.returnApproveBorrowedBook(bookId,connectedUser));
	}

	//consumes: Đây là thuộc tính của chú thích @PostMapping mà bạn dùng để chỉ định các loại MIME (Media Type) mà phương thức sẽ xử lý.
	@PostMapping(value = "/cover/{book-id}",consumes = "multipart/form-data")
	public ResponseEntity<?> uploadBookCoverPicture(
			@PathVariable("book-id") Integer bookId,
			@Parameter()
			@RequestPart("file") MultipartFile file,//@RequestPart giúp bạn trích xuất các phần cụ thể từ yêu cầu multipart/form-data
			Authentication connectedUser
	){
		bookService.uploadBookCoverPicture(file,connectedUser,bookId);
		return ResponseEntity.accepted().build();
	}



}
