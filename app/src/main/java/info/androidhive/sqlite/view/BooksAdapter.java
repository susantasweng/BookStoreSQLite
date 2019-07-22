package info.androidhive.sqlite.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import info.androidhive.sqlite.R;
import info.androidhive.sqlite.database.model.Book;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private Context context;
    private List<Book> booksList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView dot;
        public TextView author;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            dot = view.findViewById(R.id.dot);
            author = view.findViewById(R.id.author);
        }
    }


    public BooksAdapter(Context context, List<Book> booksList) {
        this.context = context;
        this.booksList = booksList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Book book = booksList.get(position);

        holder.title.setText(book.getTitle());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying author
        holder.author.setText(book.getAuthor());
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

}
