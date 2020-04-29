package com.appdevSoumitri.smiledetector;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class FaceDetectionAdapter extends RecyclerView.Adapter<FaceDetectionAdapter.ViewHolder>
{
    private ArrayList<FaceDetectorModel> faceDetectorModels;
    private Context context;

    public FaceDetectionAdapter(ArrayList<FaceDetectorModel> faceDetectorModels,Context context) {
        this.faceDetectorModels = faceDetectorModels;
        this.context=context;
    }

    @NonNull
    @Override
    public FaceDetectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.item_face_detection,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaceDetectionAdapter.ViewHolder holder, int position) {

        FaceDetectorModel faceDetectorModel=faceDetectorModels.get(position);
        /*holder.tv1.setText(String.valueOf(faceDetectorModel.getId()));
        holder.tv2.setText(faceDetectorModel.getText());*/
        holder.tv1.setText(MessageFormat.format("Face: {0}", String.valueOf(faceDetectorModel.getId())));
        holder.tv2.setText(MessageFormat.format("Face: {0}", faceDetectorModel.getText()));
    }

    @Override
    public int getItemCount() {
        return faceDetectorModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv1,tv2;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1=itemView.findViewById(R.id.tvFaceDet1);
            tv2=itemView.findViewById(R.id.tvFaceDet2);
        }
    }
}
