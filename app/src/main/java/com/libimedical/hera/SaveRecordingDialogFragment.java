package com.libimedical.hera;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.libimedical.hera.tracing.TraceViewModel;
import com.libimedical.hera.tracing.TracingItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SaveRecordingDialogFragment extends DialogFragment {

    private TraceViewModel mTraceViewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View inflatorView = inflater.inflate(R.layout.fragment_save_recording_dialog, null);

        builder.setView(inflatorView);
        builder.setPositiveButton(R.string.save, (dialog, id) -> {
            EditText record_id_text = (EditText) inflatorView.findViewById(R.id.record_id_text);
            EditText notes_text = (EditText) inflatorView.findViewById(R.id.notes_text);
            String record_id = record_id_text.getText().toString();
            String notes = notes_text.getText().toString();
            File file = renameTempAudioFile();
            TracingItem tracingItem = new TracingItem(record_id, file.getPath(), notes);
            // TODO: Might not actually by using the right ViewModelProvider, as it doesn't seem
            //       to specify the right fragment
            mTraceViewModel = ViewModelProviders.of(this).get(TraceViewModel.class);
            mTraceViewModel.insert(tracingItem);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> SaveRecordingDialogFragment.this.getDialog().cancel());
        return builder.create();
    }

    private File renameTempAudioFile() {
        // TODO: Handle IO Exceptions
        File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/audiorecordtemp.mp3");
        String dateString = new SimpleDateFormat("yyyyMMddHHmmss'.mp3'").format(new Date());
        File newDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "fetoMic/");
        newDirectory.mkdirs();
        File newFile = new File(newDirectory.getAbsolutePath(), dateString);
        if(!tempFile.renameTo(newFile)) {
            // TODO: Throw error/exception here
        }
        return newFile;
    }
}
