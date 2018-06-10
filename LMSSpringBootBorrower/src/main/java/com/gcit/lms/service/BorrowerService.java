package com.gcit.lms.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.LibraryBranchDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.LibraryBranch;
import com.gcit.lms.entity.Publisher;

@RestController
public class BorrowerService {
	
	@Autowired
	AuthorDAO adao;
	
	@Autowired
	GenreDAO gndao;
	
	@Autowired
	BookDAO bdao;
	
	@Autowired
	PublisherDAO pdao;
	
	@Autowired
	BookLoansDAO bldao;
	
	@Autowired
	BookCopiesDAO bcdao;
	
	@Autowired
	LibraryBranchDAO lbdao;
	
	@Autowired
	BorrowerDAO brdao;
	
	//BookLoan Operations
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/bookLoan",method=RequestMethod.POST,consumes="application/json")
	public void saveBookLoan(@RequestBody BookLoans bookLoan) throws SQLException
	{
			try {
				bldao.addBookLoans(bookLoan);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/bookLoans",method=RequestMethod.PUT,consumes="application/json")
	public void returnBookLoan(@RequestParam("bookId") Integer bookId,@RequestParam("branchId") Integer branchId,@RequestParam("cardNo") Integer cardNo,@RequestBody BookLoans bookLoan) throws SQLException //this method updates dateIn 
	{
			try {
				bldao.returnBookLoans(bookLoan);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/bookLoans/dueDate",method=RequestMethod.PUT, consumes="application/json")
	public void changeDueDate(@RequestParam("bookId") Integer bookId,@RequestParam("branchId") Integer branchId,@RequestParam("cardNo") Integer cardNo,@RequestBody BookLoans bookloan) throws SQLException, ParseException
	{	
		
			try {
				
				System.out.println(bookloan.getDueDate());
				bldao.changeDueDate(bookloan);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/bookLoans/user/{cardNo}",method=RequestMethod.GET, produces="application/json")
	public List<BookLoans> ReadBookLoansByUserBranch(@PathVariable("cardNo") Integer cardNo) throws SQLException
	{
			try {
				List<BookLoans> bookLoans=bldao.ReadBookLoansByUser(cardNo);
				
				for(BookLoans bookLoan:bookLoans)
				{
					List<Book> books=bdao.ReadBooksByBookID(bookLoan.getBookId());
					bookLoan.setBook(books.get(0));
					
					List<Borrower> borrowers=brdao.ReadAllBorrowerById(bookLoan.getCardNo());
					bookLoan.setBorrower(borrowers.get(0));
					
					List<LibraryBranch> libraryBranch=lbdao.ReadLibraryBranchesById(bookLoan.getBranchId());
					bookLoan.setLibraryBranch(libraryBranch.get(0));
				}
				
				return bookLoans;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		
		return null;
	}
	
	
	
	//BookCopies
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/bookCopies",method=RequestMethod.PUT,consumes="application/json")
	public void loanBookCopies(@RequestParam("bookId") Integer bookId,@RequestParam("branchId") Integer branchId,@RequestBody BookCopies bookCopy) throws SQLException //this method updates noOfCopies
	{
			try {
				bcdao.LoanBookCopies(bookCopy);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	
	//Books
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/books/{bookId}",method=RequestMethod.GET, produces="application/json")
//	public List<Book> ReadBookByBookID(@RequestParam Integer bookId) throws SQLException
	public List<Book> ReadBookByBookID(@PathVariable("bookId") Integer bookId) throws SQLException  //for return book
	{
			try {
				List<Book> books=bdao.ReadBooksByBookID(bookId);
				for(Book book:books)
				{
					//find all authors related to the book
					List<Author> authors=adao.ReadAuthorsByBookId(book.getBookId());
					book.setAuthors(authors);
					
					//find all genres related to the book
					List<Genre> genres=gndao.ReadGenresByBookId(book.getBookId());
					book.setGenres(genres);
					
					//find all publisher related to the book
					List<Publisher> publishers=pdao.ReadPublisherByBookId(book.getBookId());
					book.setPublisher(publishers.get(0));
					
				}
				return books;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	
	//Borrower
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/borrowers",method=RequestMethod.GET, produces="application/json")
	public List<Borrower> readBorrower() throws SQLException
	{
			try {
				List<Borrower> borrowers=brdao.ReadAllBorrowers();
				return borrowers;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/borrowers/name/{name}",method=RequestMethod.GET, produces="application/json")
//	public List<Borrower> readBorrowerByName(@RequestParam String name) throws SQLException
	public List<Borrower> readBorrowerByName(@PathVariable("name") String name) throws SQLException
	{
			try {
				List<Borrower> borrowers=new ArrayList<Borrower>();
				if(name.equals("undefined"))
				{
					borrowers=brdao.ReadAllBorrowers();
				}
				else {
					System.out.println("not null "+name);
					borrowers=brdao.ReadBorrowersByName(name);
				}
				
				return borrowers;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/borrowers/{cardNo}",method=RequestMethod.GET, produces="application/json")
//	public List<Borrower> readBorrowerById(@PRequestParam Integer cardNo) throws SQLException 
	public List<Borrower> readBorrowerById(@PathVariable("cardNo") Integer cardNo) throws SQLException 
	{
			try {
				List<Borrower> borrowers=new ArrayList<Borrower>();
				
					borrowers=brdao.ReadAllBorrowerById(cardNo);
				
				return borrowers;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/borrowers/{cardNo}",method=RequestMethod.PUT, consumes="application/json")
//	public void updateBorrower(@RequestBody Borrower borrower) throws SQLException
	public void updateBorrower(@PathVariable("cardNo") Integer cardNo,@RequestBody Borrower borrower) throws SQLException
	{
			try {
				brdao.updateBorrower(borrower);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/borrowers/{cardNo}",method=RequestMethod.DELETE)
	public void deleteBorrower(@PathVariable("cardNo") Integer cardNo) throws SQLException
	{
			try {
				brdao.deleteBorrower(cardNo);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
	
	@CrossOrigin
	@Transactional
	@RequestMapping(value="/borrower",method=RequestMethod.POST, consumes="application/json")
	public void saveBorrower(@RequestBody Borrower borrower) throws SQLException
	{
			try {
				brdao.addBorrower(borrower);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}

}
