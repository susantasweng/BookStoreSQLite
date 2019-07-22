package info.androidhive.sqlite.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.BookDatabaseHelper;
import info.androidhive.sqlite.database.model.Book;
import info.androidhive.sqlite.utils.MyDividerItemDecoration;
import info.androidhive.sqlite.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {
    private BooksAdapter bookListAdapterAdapter;
    private List<Book> booksList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noBooksView;
    public int currentBookId = 0;

    private BookDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noBooksView = findViewById(R.id.empty_books_view);

        db = new BookDatabaseHelper(this);

        booksList.addAll(db.getAllBooks());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBookDialog(false, null, -1);
            }
        });

        bookListAdapterAdapter = new BooksAdapter(this, booksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(bookListAdapterAdapter);

        toggleEmptyBooks();

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new title in db
     * and refreshing the list
     */
    private void createBookEntry(String title, String author) {
        // inserting title in db and getting
        // newly inserted title id
        Book book = new Book(currentBookId, title, author);
        int id = db.addBook(book);

        // get the newly inserted title from db
        Book newBook = db.getBook(id);

        if (newBook != null) {
            // adding new title to array list at 0 position
            booksList.add(0, newBook);

            // refreshing the list
            bookListAdapterAdapter.notifyDataSetChanged();

            toggleEmptyBooks();
        }

        currentBookId ++;
    }

    /**
     * Updating title in db and updating
     * item in the list by its position
     */
    private void updateBook(String bookTitle, int position) {
        Book book = booksList.get(position);
        // updating title text
        book.setTitle(bookTitle);

        // updating title in db
        db.updateBook(book);

        // refreshing the list
        booksList.set(position, book);
        bookListAdapterAdapter.notifyItemChanged(position);

        toggleEmptyBooks();
    }

    /**
     * Deleting title from SQLite and removing the
     * item from the list by its position
     */
    private void deleteBook(int position) {
        // deleting the title from db
        db.deleteBook(booksList.get(position));

        // removing the title from the list
        booksList.remove(position);
        bookListAdapterAdapter.notifyItemRemoved(position);

        toggleEmptyBooks();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showBookDialog(true, booksList.get(position), position);
                } else {
                    deleteBook(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a book.
     * when shouldUpdate=true, it automatically displays old book and changes the
     * button text to UPDATE
     */
    private void showBookDialog(final boolean shouldUpdate, final Book book, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.book_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputTitle = view.findViewById(R.id.book_title_edit);
        final EditText inputAuthor = view.findViewById(R.id.book_author_edit);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);

        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_book_title) : getString(R.string.lbl_edit_book_title));
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_book_author) : getString(R.string.lbl_edit_book_author));

        if (shouldUpdate && book != null) {
            inputTitle.setText(book.getTitle());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputTitle.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter book!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating book
                if (shouldUpdate && book != null) {
                    // update book by it's id
                    updateBook(inputTitle.getText().toString(), position);
                } else {
                    // create new book
                    createBookEntry(inputTitle.getText().toString(), inputAuthor.getText().toString());
                }
            }
        });
    }

    /**
     * Toggling list and empty books view
     */
    private void toggleEmptyBooks() {
        // you can check booksList.size() > 0

        if (db.getBooksCount() > 0) {
            noBooksView.setVisibility(View.GONE);
        } else {
            noBooksView.setVisibility(View.VISIBLE);
        }
    }
}
