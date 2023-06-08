package com.kar.kfd.gov.kfdsurvey.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.base.BaseAdapter;
import com.kar.kfd.gov.kfdsurvey.model.KnownSpeciesModel;

import java.util.List;

public class KnownSpeciesAdapter extends BaseAdapter<KnownSpeciesModel> {



    public KnownSpeciesAdapter(Context ctx, List<KnownSpeciesModel> list, RecyclerView rv, RecyclerView.LayoutManager lm) {
        super(ctx, list, rv, lm);

    }

    @Override
    protected AdapterHolderBase initUI(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.list_unknown_species, parent, false));
    }

    @Override
    protected void initListeners(AdapterHolderBase holder, int position) {

        KnownSpeciesAdapter.ViewHolder viewHolder = (ViewHolder) holder;

        KnownSpeciesModel speciesModel = mList.get(position);

        viewHolder.tvKnownSpecies.setText(speciesModel.getSpeciesName());
        viewHolder.etNoOfKnownSpecies.setText(String.valueOf(speciesModel.getSpeciesCount()));
        viewHolder.etNoOfKnownSpecies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!viewHolder.etNoOfKnownSpecies.getText().toString().isEmpty()) {
                    try {
                        speciesModel.setSpeciesCount(Integer.parseInt(viewHolder.etNoOfKnownSpecies.getText().toString()));
                    } catch (Exception e) {
                        speciesModel.setSpeciesCount(0);
                    }


                }

            }
        });


    }

    private class ViewHolder extends AdapterHolderBase {


        TextView tvKnownSpecies;
        EditText etNoOfKnownSpecies;

        public ViewHolder(View itemView) {
            super(itemView);

            tvKnownSpecies = itemView.findViewById(R.id.tvKnownSpecies);
            etNoOfKnownSpecies = itemView.findViewById(R.id.etNoOfKnownSpecies);
        }
    }
}
