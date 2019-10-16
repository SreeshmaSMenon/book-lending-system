package com.hcl.booklendingsystem.service;

import static com.hcl.booklendingsystem.util.BookLendingSystemConstants.BOOK_NOT_AVAILABLE;
import static com.hcl.booklendingsystem.util.BookLendingSystemConstants.BORROW;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hcl.booklendingsystem.entity.Book;
import com.hcl.booklendingsystem.entity.BookHistory;
import com.hcl.booklendingsystem.exception.CommonException;
import com.hcl.booklendingsystem.repository.BookHistoryRepository;
import com.hcl.booklendingsystem.repository.BookRepository;

@Service
public class BookScheduler {
	@Autowired
	BookService bookService;

	@Autowired
	BookHistoryRepository bookHistoryRepository;

	@Autowired
	BookRepository bookRepository;

	public static final Logger LOGGER = LoggerFactory.getLogger(BookScheduler.class);

	@Scheduled(fixedRate = 2000)
	public void releaseBook() {
		LOGGER.info(" releaseBook schedular at " + LocalDateTime.now());
		LocalDateTime bookExpiredDate = LocalDateTime.now().minusMinutes(2);
		Optional<List<BookHistory>> booksOpt = bookHistoryRepository.findByBorrowDateLessThan(bookExpiredDate);
		booksOpt.ifPresent(bookHistorys -> {
			bookHistorys.forEach(bookHistory -> {
				Optional<Book> books = bookRepository.findById(bookHistory.getBookId());
				if (!books.isPresent())
					throw new CommonException(BOOK_NOT_AVAILABLE);

				books.get().setBookStatus(BORROW);
				bookRepository.save(books.get());

			});

		});
		LOGGER.info(" releaseBook schedular completed ");

	}

}