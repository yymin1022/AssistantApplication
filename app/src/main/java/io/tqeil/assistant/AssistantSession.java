package io.tqeil.assistant;

import android.app.VoiceInteractor;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.service.voice.VoiceInteractionSession;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Arrays;

public class AssistantSession extends VoiceInteractionSession{
    String TAG = "Assistant Session";

    private static final int STATE_IDLE = 0;
    private static final int STATE_LAUNCHING = 1;
    private static final int STATE_CONFIRM = 2;
    private static final int STATE_PICK_OPTION = 3;
    private static final int STATE_COMMAND = 4;
    private static final int STATE_ABORT_VOICE = 5;
    private static final int STATE_COMPLETE_VOICE = 6;

    private int mCurrentTask = -1;
    private int mState = STATE_IDLE;

    CharSequence mPendingPrompt;
    ChatListAdapter chatListAdapter;
    ImageView imageCharacter;
    ListView listChat;
    Request mPendingRequest;
    VoiceInteractor.PickOptionRequest.Option[] mPendingOptions;

    AssistantSession(Context context){
        super(context);
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onShow(Bundle args, int showFlags){
        super.onShow(args, showFlags);
    }

    @Override
    public void onHide(){
        super.onHide();
    }

    @Override
    public View onCreateContentView(){
        View mContentView = getLayoutInflater().inflate(R.layout.voice_interaction_session, null);

        imageCharacter = mContentView.findViewById(R.id.assistant_image_character);
        imageCharacter.setImageResource(R.drawable.ic_assistant_character_stop);

        ImageView btnSpeak = mContentView.findViewById(R.id.assistant_btn_speak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addConversation();
            }
        });

        chatListAdapter = new ChatListAdapter();
        listChat = mContentView.findViewById(R.id.assistant_list_chat);
        listChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listChat.setStackFromBottom(true);
        listChat.setDividerHeight(0);
        listChat.setAdapter(chatListAdapter);

        return mContentView;
    }

    @Override
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content){
        if (content != null){
            Log.i(TAG, "Assist intent: " + content.getIntent());
            Log.i(TAG, "Assist clipdata: " + content.getClipData());
        }
        if (data != null){
            Uri referrer = data.getParcelable(Intent.EXTRA_REFERRER);
            if (referrer != null){
                Log.i(TAG, "Referrer: " + referrer);
            }
        }
    }

    @Override
    public void onHandleScreenshot(Bitmap screenshot){
    }

    @Override
    public void onComputeInsets(Insets outInsets){
        super.onComputeInsets(outInsets);
        if(mState != STATE_IDLE){
            outInsets.touchableInsets = Insets.TOUCHABLE_INSETS_CONTENT;
        }
    }

    @Override
    public void onTaskStarted(Intent intent, int taskId){
        super.onTaskStarted(intent, taskId);
        mCurrentTask = taskId;
    }

    @Override
    public void onTaskFinished(Intent intent, int taskId){
        super.onTaskFinished(intent, taskId);
        if (mCurrentTask == taskId){
            mCurrentTask = -1;
        }
    }

    @Override
    public void onLockscreenShown(){
        if (mCurrentTask < 0){
            hide();
        }
    }

    @Override
    public boolean[] onGetSupportedCommands(String[] commands){
        boolean[] res = new boolean[commands.length];
        for (int i=0; i<commands.length; i++){
            if ("io.tqeil.assistant.COMMAND".equals(commands[i])){
                res[i] = true;
            }
        }
        return res;
    }

    @Override
    public void onRequestConfirmation(ConfirmationRequest request){
        Log.i(TAG, "onConfirm: prompt=" + request.getVoicePrompt() + " extras=" + request.getExtras());
        setPrompt(request.getVoicePrompt());
        mPendingRequest = request;
        mState = STATE_CONFIRM;
    }

    @Override
    public void onRequestPickOption(PickOptionRequest request){
        Log.i(TAG, "onPickOption: prompt=" + request.getVoicePrompt() + " options=" + Arrays.toString(request.getOptions()) + " extras=" + request.getExtras());
        mPendingRequest = request;
        setPrompt(request.getVoicePrompt());
        mPendingOptions = request.getOptions();
        mState = STATE_PICK_OPTION;
    }

    @Override
    public void onRequestCompleteVoice(CompleteVoiceRequest request){
        Log.i(TAG, "onCompleteVoice: message=" + request.getVoicePrompt() + " extras=" + request.getExtras());
        setPrompt(request.getVoicePrompt());
        mPendingRequest = request;
        mState = STATE_COMPLETE_VOICE;
    }

    @Override
    public void onRequestAbortVoice(AbortVoiceRequest request){
        Log.i(TAG, "onAbortVoice: message=" + request.getVoicePrompt() + " extras=" + request.getExtras());
        setPrompt(request.getVoicePrompt());
        mPendingRequest = request;
        mState = STATE_ABORT_VOICE;
    }

    @Override
    public void onRequestCommand(CommandRequest request){
        Bundle extras = request.getExtras();
        if (extras != null){
            extras.getString("arg");
        }
        Log.i(TAG, "onCommand: command=" + request.getCommand() + " extras=" + extras);
        mPendingRequest = request;
        mState = STATE_COMMAND;
    }

    @Override
    public void onCancelRequest(Request request){
        Log.i(TAG, "onCancel");
        if (mPendingRequest == request){
            mPendingRequest = null;
            mState = STATE_LAUNCHING;
        }
        request.cancel();
    }

    private void setPrompt(VoiceInteractor.Prompt prompt){
        if (prompt == null){
            mPendingPrompt = "";
        } else{
            mPendingPrompt = prompt.getVisualPrompt();
        }
    }

    private void addConversation(){
        animateImage(true);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                animateImage(false);
                switch((int)(Math.random() * 5)){
                    case 0:
                        chatListAdapter.add("Hello!", 1);
                        chatListAdapter.add("Good Morning!", 0);
                        break;
                    case 1:
                        chatListAdapter.add("How's the weather today?", 1);
                        chatListAdapter.add("It's sunny day!", 0);
                        break;
                    case 2:
                        chatListAdapter.add("What is tomorrow schedule?", 1);
                        chatListAdapter.add("You have a meeting with A Comapany at 3 o'clock.", 0);
                        break;
                    case 3:
                        chatListAdapter.add("Tell me the newses.", 1);
                        chatListAdapter.add("Google announced Android 11 yesterday.", 0);
                        break;
                    case 4:
                        chatListAdapter.add("What is the value of Pi?", 1);
                        chatListAdapter.add("It is 3.14159265358979.... I don't know more than this!", 0);
                        break;
                }
                chatListAdapter.notifyDataSetChanged();
            }
        }, 1000);
    }

    private void animateImage(boolean state){
        if(state){
            GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(imageCharacter);
            Glide.with(getContext()).load(R.drawable.image_assistant_character).into(gifImage);
        }else{
            imageCharacter.setImageResource(R.drawable.ic_assistant_character_stop);
        }
    }
}
