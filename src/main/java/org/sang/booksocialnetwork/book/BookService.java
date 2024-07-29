package org.sang.booksocialnetwork.book;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.ListJoin;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.naming.OperationNotSupportedException;
import lombok.RequiredArgsConstructor;
import org.sang.booksocialnetwork.common.PageResponse;
import org.sang.booksocialnetwork.exception.OperationNotPermittedException;
import org.sang.booksocialnetwork.file.FileStorageService;
import org.sang.booksocialnetwork.history.BookTransactionHistory;
import org.sang.booksocialnetwork.history.BookTransactionHistoryRepository;
import org.sang.booksocialnetwork.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
	private final BookMapper bookMapper;
	private final FileStorageService fileStorageService;

	public Integer save(BookRequest request, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Book book = bookMapper.toBook(request);
		book.setOwner(user);

		return bookRepository.save(book).getId();

	}

	//này trả về Book
	private Book findBookById(Integer bookId) {
		return bookRepository.findById(bookId)
				.orElseThrow(() -> new EntityNotFoundException("No book found with Id: " + bookId));
	}

	//này trả về Book Response
	public BookResponse findById(Integer bookId) {
		return bookRepository.findById(bookId)
				.map(bookMapper::toBookResponse)
				.orElseThrow(() -> new EntityNotFoundException("No book found with the Id: " + bookId));
	}

	public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
		List<BookResponse> bookResponses = books.stream()
				.map(bookMapper::toBookResponse)
				.toList();
		return new PageResponse<>(
				bookResponses,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast()
		);
	}

	public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
		List<BookResponse> bookResponses = books.stream()
				.map(bookMapper::toBookResponse)
				.toList();
		return new PageResponse<>(
				bookResponses,
				books.getNumber(),
				books.getSize(),
				books.getTotalElements(),
				books.getTotalPages(),
				books.isFirst(),
				books.isLast()
		);

	}

	public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,
				user.getId());
		List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
				.map(bookMapper::toBorrowedBookResponse)
				.toList();
		return new PageResponse<>(
				bookResponse,
				allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast()
		);
	}

	public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
		User user = ((User) connectedUser.getPrincipal());
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
		Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,
				user.getId());
		List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
				.map(bookMapper::toBorrowedBookResponse)
				.toList();
		return new PageResponse<>(
				bookResponse,
				allBorrowedBooks.getNumber(),
				allBorrowedBooks.getSize(),
				allBorrowedBooks.getTotalElements(),
				allBorrowedBooks.getTotalPages(),
				allBorrowedBooks.isFirst(),
				allBorrowedBooks.isLast()
		);
	}

	// Check if the user is the owner of the book
	private boolean checkOwnership(Book book, User user) {
		return Objects.equals(book.getOwner().getId(), user.getId());

	}

	//in lỗi cua checkOwnership
	private void verifyOwnership(String msg) {
		throw new OperationNotPermittedException(msg);
	}

	// Check if the book is archived or not shareable
	private void checkBookAvailability(Book book) {
		if (book.isArchived() || !book.isShareable()) {
			throw new OperationNotPermittedException(
					"The requested book cannot be borrowed since it is archived or not shareable.");
		}
	}

	public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
		Book book = findBookById(bookId);
		User user = ((User) connectedUser.getPrincipal());
		if (!checkOwnership(book, user)) {
			verifyOwnership("You can not update others books shareable status");
		}
		book.setShareable(!book.isShareable());
		bookRepository.save(book);
		return bookId;
	}

	public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
		Book book = findBookById(bookId);
		User user = ((User) connectedUser.getPrincipal());
		if (!checkOwnership(book, user)) {
			verifyOwnership("You can not update others books archived status");
		}
		book.setArchived(!book.isArchived());
		bookRepository.save(book);
		return bookId;
	}

	public Integer borrowBook(Integer bookId, Authentication connectedUser) {
		Book book = findBookById(bookId);
		//check xem cuon sach duoc muong co duoc luu tru va duoc chia se không
		checkBookAvailability(book);
		User user = ((User) connectedUser.getPrincipal());
		//check xem bạn không thê mượn cuon sach cua chính ban
		if (checkOwnership(book, user)) {
			verifyOwnership("You can not borrow your own book");
		}
		//check xem cuong sach duoc mượn hay chưa
		final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId,
				user.getId());
		if (isAlreadyBorrowed) {
			throw new OperationNotPermittedException("The request book is already borrowed");
		}
		BookTransactionHistory history = BookTransactionHistory.builder()
				.user(user)
				.book(book)
				.returned(false)//set tra chưa thành false
				.returnApproved(false)//set xac nhan chưa trả sách thành false
				.build();
		return bookTransactionHistoryRepository.save(history).getId();
	}

	public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
		Book book = findBookById(bookId);
		checkBookAvailability(book);
		User user = ((User) connectedUser.getPrincipal());
		if (checkOwnership(book, user)) {
			verifyOwnership("You can not borrow your own book");
		}
		//check user already borrow its book
		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId,
						user.getId())
				.orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
		bookTransactionHistory.setReturned(true);
		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

	}

	public Integer returnApproveBorrowedBook(Integer bookId, Authentication connectedUser) {
		Book book = findBookById(bookId);
		checkBookAvailability(book);
		User user = ((User) connectedUser.getPrincipal());
		if (checkOwnership(book, user)) {
			verifyOwnership("You can not borrow or return your own book");
		}
		BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId,
						user.getId())
				.orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet.You cannot "
						+ "approved it"));
		bookTransactionHistory.setReturnApproved(true);
		return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

	}

	public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
		Book book=findBookById(bookId);
		User user = ((User) connectedUser.getPrincipal());
		var bookCover=fileStorageService.saveFile(file,user.getId());
		book.setBookCover(bookCover);
		bookRepository.save(book);
	}
}
