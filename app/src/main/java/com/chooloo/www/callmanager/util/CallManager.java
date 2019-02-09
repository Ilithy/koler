package com.chooloo.www.callmanager.util;

import android.content.Context;
import android.telecom.Call;
import android.telecom.VideoProfile;

import com.chooloo.www.callmanager.database.Contact;
import com.chooloo.www.callmanager.activity.OngoingCallActivity;

import timber.log.Timber;

public class CallManager {

    // Variables
    public static Call sCall;

    // -- Call Actions -- //

    /**
     * Answers incoming call
     */
    public static void sAnswer() {
        if (sCall != null) {
            sCall.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    /**
     * Ends call
     * If call ended from the other side, disconnects
     */
    public static void sReject() {
        if (sCall != null) {
            if (sCall.getState() == Call.STATE_RINGING) {
                sCall.reject(false, null);
            } else {
                try {
                    sCall.disconnect();
                } catch (Exception e1) {
                    Timber.e(e1, "Couldn't disconnect call (Trying to reject)");
                    sCall.reject(false, null);
                }
            }
        }
    }

    public static void sHold(boolean hold) {
        if (sCall != null) {
            if (hold) sCall.hold();
            else sCall.unhold();
        }
    }

    /**
     * Registers a Callback object to the current call
     *
     * @param callback the callback to register
     */
    public static void registerCallback(OngoingCallActivity.Callback callback) {
        if (sCall == null) return;
        sCall.registerCallback(callback);
    }

    /**
     * Unregisters the Callback from the current call
     *
     * @param callback the callback to unregister
     */
    public static void unregisterCallback(Call.Callback callback) {
        if (sCall == null) return;
        sCall.unregisterCallback(callback);
    }

    // -- Getters -- //

    /**
     * Gets the phone number of the contact from the end side of the current call
     * in the case of a voicemail number, returns "Voicemail"
     *
     * @return String - phone number, or voicemail. if not recognized, return null.
     */
    public static Contact getDisplayContact(Context context) {

        if (sCall == null) return ContactsManager.UNKNOWN;

        String uri = sCall.getDetails().getHandle().toString(); // Callers details

        if (uri.contains("voicemail")) // If uri contains 'voicemail' this is a... voicemail dah
            return ContactsManager.VOICEMAIL;

        String telephoneNumber = null;

        if (uri.contains("tel")) // If uri contains 'tel' this is a normal number
            telephoneNumber = uri.replace("tel:", "");

        if (telephoneNumber == null || telephoneNumber.isEmpty()) return ContactsManager.UNKNOWN; // Unknown number

        Contact contact = ContactsManager.getContactByPhoneNumber(context, telephoneNumber); // Get the contacts with the number
        if (contact == null) return new Contact(telephoneNumber, telephoneNumber, null); // No known contacts for the number, return the number

        return contact;
    }

    /**
     * Returnes the current state of the call from the Call object (named sCall)
     *
     * @return Call.State
     */
    public static int getState() {
        if (sCall == null) return Call.STATE_DISCONNECTED;
        return sCall.getState();
    }
}
