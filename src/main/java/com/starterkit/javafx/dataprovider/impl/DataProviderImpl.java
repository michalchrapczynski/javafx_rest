package com.starterkit.javafx.dataprovider.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.starterkit.javafx.dataprovider.DataProvider;
import com.starterkit.javafx.dataprovider.data.AuthorVO;
import com.starterkit.javafx.dataprovider.data.BookVO;

/**
 * Provides data. Data is stored locally in this object. Additionally a call
 * delay is simulated.
 *
 * @author Leszek
 */
public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	/**
	 * Delay (in ms) for method calls.
	 */
	private static final long CALL_DELAY = 3000;

	private Collection<BookVO> books = new ArrayList<>();

	public DataProviderImpl() {

		List<AuthorVO> authors = new ArrayList<>();
		authors.add(new AuthorVO("Jan", "Nowak"));
		List<AuthorVO> authors2 = new ArrayList<>();
		authors2.add(new AuthorVO("Joanna", "Kowalska"));
		books.add(new BookVO("Moon", authors));
		books.add(new BookVO("Morze", authors2));
		books.add(new BookVO("Anastazja", authors2));
		books.add(new BookVO("Azerbejdżan", authors));
		books.add(new BookVO("Kamień", authors));
		books.add(new BookVO("Jezioro", authors));
		books.add(new BookVO("Pani z jeziora", authors2));
		books.add(new BookVO("Autostrada", authors));
		books.add(new BookVO("Lodowiec", authors2));

	}

	@Override
	public Collection<BookVO> findTitle(String title) {
		LOG.debug("Entering findPersons()");

		try {
			Thread.sleep(CALL_DELAY);
		} catch (InterruptedException e) {
			throw new RuntimeException("Thread interrupted", e);
		}

		Collection<BookVO> result = books.stream().filter(p -> //
		((title == null || title.isEmpty()) || (title != null && !title.isEmpty() && p.getTitle().contains(title))))
				.collect(Collectors.toList());

		LOG.debug("Leaving findPersons()");
		return result;
	}
}
