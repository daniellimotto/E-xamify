package com.example.e_xamify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<Quiz> quizzes;
    private OnItemClickListener listener;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnItemClickListener {
        void onItemClick(Quiz quiz);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Quiz quiz);
    }

    public QuizAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.titleTextView.setText(quiz.getQuizTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(quizzes.get(position));
            }
        });

        holder.deleteQuizButton.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button deleteQuizButton;

        QuizViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.quizTitle);
            deleteQuizButton = itemView.findViewById(R.id.deleteQuizButton);
        }
    }
}