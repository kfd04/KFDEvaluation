package com.kar.kfd.gov.kfdsurvey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.kar.kfd.gov.kfdsurvey.R;
import com.kar.kfd.gov.kfdsurvey.base.BaseAdapter;
import com.kar.kfd.gov.kfdsurvey.model.SMCModel;

import java.util.List;

public class SMCAdapter extends BaseAdapter<SMCModel>{

    private SMCAdapter.ViewCallBack mCallback;

    private Context context;
    public boolean valueChanged = false;

    public SMCAdapter(Context ctx, List<SMCModel> list, RecyclerView rv, RecyclerView.LayoutManager lm, SMCAdapter.ViewCallBack viewCallBack) {
        super(ctx, list, rv, lm);
        mCallback=viewCallBack;
        context =ctx;
    }

    public interface ViewCallBack {
        void viewCallBack(SMCModel smcModel);
    }

    @Override
    protected AdapterHolderBase initUI(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.view_smc_table,parent,false));
    }

    @Override
    protected void initListeners(AdapterHolderBase holder, int position) {

        SMCAdapter.ViewHolder viewHolder = (ViewHolder) holder;

        SMCModel smcModel = mList.get(position);

        viewHolder.tvSMCWorks.setText(smcModel.getSmcWorks());
        viewHolder.tvSMCExpenditure.setText(String.valueOf(smcModel.getSmcCost()));
        viewHolder.rgSmc.setOnCheckedChangeListener(null);
        if (smcModel.getExist()==1)
        viewHolder.rbExist.setChecked(true);
        else
         viewHolder.rbNonExist.setChecked(true);

        viewHolder.rgSmc.setOnCheckedChangeListener((group, checkedId) -> {

            if (R.id.rbExist == checkedId) {
                smcModel.setExist(1);
            } else
                smcModel.setExist(0);

            valueChanged = true;

        });

    }

    private  class ViewHolder extends AdapterHolderBase  {

        TextView tvSMCWorks,tvSMCExpenditure;
        RadioGroup rgSmc;
        RadioButton rbExist,rbNonExist;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSMCWorks = itemView.findViewById(R.id.tvSMCWorks);
            tvSMCExpenditure=itemView.findViewById(R.id.tvSMCExpenditure);
            rgSmc = itemView.findViewById(R.id.rgSmc);
            rbExist =itemView.findViewById(R.id.rbExist);
            rbNonExist=itemView.findViewById(R.id.rbNonExist);


         /*   chkExist=itemView.findViewById(R.id.chkExist);
            chkExist=itemView.findViewById(R.id.chkNonExist);*/
        }


    }
}
