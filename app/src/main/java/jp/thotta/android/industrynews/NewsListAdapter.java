package jp.thotta.android.industrynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by thotta on 2016/05/10.
 */
public class NewsListAdapter extends ArrayAdapter<News> {

    public NewsListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) getContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.news_row, null);
        }
        News news = getItem(position);
        TextView titleTextView =
                (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView descriptionTextView =
                (TextView) convertView.findViewById(R.id.textViewDescription);
        titleTextView.setText(news.getTitle());
        descriptionTextView.setText(news.getDescription());
        convertView.setTag(news);
        return convertView;
    }
}