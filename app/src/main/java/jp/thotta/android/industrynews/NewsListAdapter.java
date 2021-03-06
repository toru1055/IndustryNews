package jp.thotta.android.industrynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        TextView titleTextView =
                (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView descriptionTextView =
                (TextView) convertView.findViewById(R.id.textViewDescription);
        TextView industryTextView =
                (TextView) convertView.findViewById(R.id.textViewIndustry);
        TextView pubDateTextView =
                (TextView) convertView.findViewById(R.id.textViewPubDate);
        TextView subscriptionTextView =
                (TextView) convertView.findViewById(R.id.textViewSubscription);

        News news = getItem(position);
        titleTextView.setText(news.getTitle());
        descriptionTextView.setText(news.getDescription());
        industryTextView.setText(news.getIndustryName());
        pubDateTextView.setText(news.getPubDate().toString());
        subscriptionTextView.setText("(" + news.getSubscriptionName() + ")");

        convertView.setTag(news);
        return convertView;
    }
}