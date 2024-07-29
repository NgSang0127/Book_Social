package org.sang.booksocialnetwork.feedback;

import java.util.Objects;
import org.sang.booksocialnetwork.book.Book;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {

	public Feedback toFeedback(FeedbackRequest request) {
		return Feedback.builder()
				.note(request.note())
				.comment(request.comment())
				.book(Book.builder()
						.id(request.bookId())
						.archived(false)
						.shareable(false)
						.build())
				.build();
	};

	public FeedbackResponse toFeedbackResponse(Feedback feedback,Integer id){
		return FeedbackResponse.builder()
				.note(feedback.getNote())
				.comment(feedback.getComment())
				.ownFeedback(Objects.equals(feedback.getCreateBy(),id))
				.build();
	}
}
