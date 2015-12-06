package com.starterkit.javafx.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.starterkit.javafx.dataprovider.DataProvider;
import com.starterkit.javafx.dataprovider.data.BookVO;
import com.starterkit.javafx.model.BookSearch;
import com.starterkit.javafx.textospeech.Speaker;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller for the person search screen.
 * <p>
 * The JavaFX runtime will inject corresponding objects in the @FXML annotated
 * fields. The @FXML annotated methods will be called by JavaFX runtime at
 * specific points in time.
 * </p>
 *
 * @author Leszek
 */
public class BookSearchController {

	private static final Logger LOG = Logger.getLogger(BookSearchController.class);

	/**
	 * Resource bundle loaded with this controller. JavaFX injects a resource
	 * bundle specified in {@link FXMLLoader#load(URL, ResourceBundle)} call.
	 * <p>
	 * NOTE: The variable name must be {@code resources}.
	 * </p>
	 */
	@FXML
	private ResourceBundle resources;

	/**
	 * URL of the loaded FXML file. JavaFX injects an URL specified in
	 * {@link FXMLLoader#load(URL, ResourceBundle)} call.
	 * <p>
	 * NOTE: The variable name must be {@code location}.
	 * </p>
	 */
	@FXML
	private URL location;

	/**
	 * JavaFX injects an object defined in FXML with the same "fx:id" as the
	 * variable name.
	 */
	@FXML
	private TextField titleField;

	@FXML
	private Button searchButton;

	@FXML
	private TableView<BookVO> resultTable;

	@FXML
	private TableColumn<BookVO, String> titleColumn;

	@FXML
	private TableColumn<BookVO, String> firstNameColumn;

	@FXML
	private TableColumn<BookVO, String> lastNameColumn;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final Speaker speaker = Speaker.INSTANCE;

	private final BookSearch model = new BookSearch();

	/**
	 * The JavaFX runtime instantiates this controller.
	 * <p>
	 * The @FXML annotated fields are not yet initialized at this point.
	 * </p>
	 */
	public BookSearchController() {
		LOG.debug("Constructor: nameField = " + titleField);
	}

	@FXML
	private void initialize() {
		LOG.debug("initialize(): nameField = " + titleField);

		initializeResultTable();

		titleField.textProperty().bindBidirectional(model.titleProperty());
		resultTable.itemsProperty().bind(model.resultProperty());

		searchButton.disableProperty().bind(titleField.textProperty().isEmpty());
	}

	private void initializeResultTable() {

		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));

		/*
		 * REV: czy napewno ma by wyswietlany tylko pierwszy autor
		 */
		firstNameColumn.setCellValueFactory(
				cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors().get(0).getFirstName()));

		lastNameColumn.setCellValueFactory(
				cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors().get(0).getLastName()));

		resultTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BookVO>() {

			@Override
			public void changed(ObservableValue<? extends BookVO> observable, BookVO oldValue, BookVO newValue) {
				LOG.debug(newValue + " selected");

				Task<Void> backgroundTask = new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						speaker.say(newValue.getTitle());
						return null;
					}
				};
				new Thread(backgroundTask).start();
			}
		});
	}

	@FXML
	private void searchButtonAction(ActionEvent event) {
		LOG.debug("'Search' button clicked");

		// searchButtonActionVersion1();
		// searchButtonActionVersion2();
		searchButtonActionVersion3();
	}

	/*
	 * REV: ta metoda nie jest uzywana
	 */
	/**
	 * <b>This implementation is INCORRECT!<b>
	 * <p>
	 * The {@link DataProvider#findPersons(String, SexVO)} call is executed in
	 * the JavaFX Application Thread.
	 * </p>
	 */
	private void searchButtonActionVersion1() {
		LOG.debug("INCORRECT implementation!");

		/*
		 * Get the data.
		 */
		Collection<BookVO> result = dataProvider.findTitle( //
				model.getTitle());//

		/*
		 * Copy the result to model.
		 */
		model.setResult(new ArrayList<BookVO>(result));

		/*
		 * Reset sorting in the result table.
		 */
		resultTable.getSortOrder().clear();
	}

	/*
	 * REV: ta metoda nie jest uzywana
	 */
	/**
	 * This implementation is correct.
	 * <p>
	 * The {@link DataProvider#findPersons(String, SexVO)} call is executed in a
	 * background thread.
	 * </p>
	 */
	private void searchButtonActionVersion2() {
		/*
		 * Use task to execute the potentially long running call in background
		 * (separate thread), so that the JavaFX Application Thread is not
		 * blocked.
		 */
		Task<Collection<BookVO>> backgroundTask = new Task<Collection<BookVO>>() {

			/**
			 * This method will be executed in a background thread.
			 */
			@Override
			protected Collection<BookVO> call() throws Exception {
				LOG.debug("call() called");

				/*
				 * Get the data.
				 */
				Collection<BookVO> result = dataProvider.findTitle( //
						model.getTitle());

				/*
				 * Value returned from this method is stored as a result of task
				 * execution.
				 */
				return result;
			}

			/**
			 * This method will be executed in the JavaFX Application Thread
			 * when the task finishes.
			 */
			@Override
			protected void succeeded() {
				LOG.debug("succeeded() called");

				/*
				 * Get result of the task execution.
				 */
				Collection<BookVO> result = getValue();

				/*
				 * Copy the result to model.
				 */
				model.setResult(new ArrayList<BookVO>(result));

				/*
				 * Reset sorting in the result table.
				 */
				resultTable.getSortOrder().clear();
			}
		};

		/*
		 * Start the background task. In real life projects some framework
		 * manages background tasks. You should never create a thread on your
		 * own.
		 */
		new Thread(backgroundTask).start();
	}

	/**
	 * This implementation is correct.
	 * <p>
	 * The {@link DataProvider#findPersons(String, SexVO)} call is executed in a
	 * background thread.
	 * </p>
	 */
	private void searchButtonActionVersion3() {
		/*
		 * Use runnable to execute the potentially long running call in
		 * background (separate thread), so that the JavaFX Application Thread
		 * is not blocked.
		 */
		Runnable backgroundTask = new Runnable() {

			/**
			 * This method will be executed in a background thread.
			 */
			@Override
			public void run() {
				LOG.debug("backgroundTask.run() called");

				/*
				 * Get the data.
				 */
				Collection<BookVO> result = dataProvider.findTitle( //
						model.getTitle());

				/*
				 * Add an event(runnable) to the event queue.
				 */
				Platform.runLater(new Runnable() {

					/**
					 * This method will be executed in the JavaFX Application
					 * Thread.
					 */
					@Override
					public void run() {
						LOG.debug("Platform.runLater(Runnable.run()) called");

						/*
						 * Copy the result to model.
						 */
						model.setResult(new ArrayList<BookVO>(result));

						/*
						 * Reset sorting in the result table.
						 */
						resultTable.getSortOrder().clear();
					}
				});
			}
		};

		/*
		 * Start the background task. In real life projects some framework
		 * manages threads. You should never create a thread on your own.
		 */
		new Thread(backgroundTask).start();
	}

}
