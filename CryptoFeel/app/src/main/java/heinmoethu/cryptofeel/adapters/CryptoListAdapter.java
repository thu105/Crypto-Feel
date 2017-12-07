package heinmoethu.cryptofeel.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Locale;

import heinmoethu.cryptofeel.R;
import heinmoethu.cryptofeel.CryptoCollection;
import heinmoethu.cryptofeel.activities.CryptoActivity;
import heinmoethu.cryptofeel.models.CryptoModel;

public class CryptoListAdapter extends RecyclerView.Adapter<CryptoListAdapter.CryptoViewHolder> {

    @Override
    public CryptoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.cell_crypto,parent,false);
        return new CryptoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CryptoListAdapter.CryptoViewHolder holder, int position) {
        CryptoModel crypto = CryptoCollection.GetInstance().getCryptos().get(position);
        holder.setup(crypto);
    }

    public int getItemCount(){
        return CryptoCollection.GetInstance().getCryptos().size();
    }


    class CryptoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private CryptoModel crypto;
        private Button btn_rank;
        private TextView tv_title, tv_price, tv_change, tv_sentiment;
        private ImageView iv_icon;
        CryptoViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            this.tv_title=itemView.findViewById(R.id.tv_title);
            this.tv_price=itemView.findViewById(R.id.tv_price);
            this.tv_change=itemView.findViewById(R.id.tv_change);
            this.tv_sentiment=itemView.findViewById(R.id.tv_sentiment);
            this.btn_rank=itemView.findViewById(R.id.btn_rank);
            this.iv_icon=itemView.findViewById(R.id.iv_icon);
        }
        void setup(CryptoModel crypto){
            this.crypto=crypto;
            this.tv_title.setText(crypto.getTitle());
            this.tv_price.setText("$ "+ Double.toString(crypto.getPrice()));
            this.tv_change.setText(String.format(Locale.US,"(%.2f %%)",crypto.getChange()));
            if(crypto.getChange()<0){
                tv_change.setTextColor(Color.parseColor("#DD0000"));
            }
            else{
                tv_change.setTextColor(Color.parseColor("#00DD00"));
            }
            this.tv_sentiment.setText(String.format(Locale.US,"%.2f",crypto.getSentiment()));
            if (crypto.getSentiment() < 0) {
                tv_sentiment.setTextColor(Color.parseColor("#DD0000"));
            } else {
                tv_sentiment.setTextColor(Color.parseColor("#00DD00"));
            }
            this.btn_rank.setText(String.format(Locale.US,"%d",crypto.getRank()));
            try {
                Field id = R.drawable.class.getDeclaredField(crypto.getImg());
                iv_icon.setImageResource(id.getInt(id));
            } catch (NoSuchFieldException e) {
                iv_icon.setImageResource(R.drawable.icon_placeholder);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            Intent cryptoIntent =new Intent(view.getContext(), CryptoActivity.class);
            cryptoIntent.putExtra(CryptoActivity.EXTRA_CRYPTO_ID,this.crypto.getId());
            view.getContext().startActivity(cryptoIntent);
        }


    }

}
