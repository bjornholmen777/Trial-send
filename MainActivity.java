package com.example.smssender;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText delayEditText;
    private EditText messageEditText;

    private static final int PICK_CSV_FILE = 1;
    private List<Lead> leads;
    private long delayMs = 1000; // Délai par défaut de 1000ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        delayEditText = findViewById(R.id.delayEditText);
        messageEditText = findViewById(R.id.messageEditText);

        // Bouton pour charger le fichier CSV
        findViewById(R.id.uploadButton).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/csv");
            startActivityForResult(intent, PICK_CSV_FILE);
        });

        // Bouton pour démarrer l'envoi des SMS
        findViewById(R.id.startButton).setOnClickListener(view -> {
            if (leads != null && !leads.isEmpty()) {
                String messageTemplate = messageEditText.getText().toString();
                String delayText = delayEditText.getText().toString();
                if (!delayText.isEmpty()) {
                    delayMs = Long.parseLong(delayText); // Mettre à jour le délai
                }
                sendSmsMessages(messageTemplate);
            } else {
                Toast.makeText(MainActivity.this, "Veuillez d'abord charger un fichier CSV", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CSV_FILE && resultCode == RESULT_OK) {
            try {
                // Lire le fichier CSV
                FileReader fileReader = new FileReader(data.getData().getPath());
                CSVReader csvReader = new CSVReader(fileReader);
                List<String[]> rows = csvReader.readAll();
                leads = parseCsvToLeads(rows);
                csvReader.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erreur de lecture du fichier CSV", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private List<Lead> parseCsvToLeads(List<String[]> rows) {
        // Convertir les lignes CSV en objets Lead
        List<Lead> leadsList = new ArrayList<>();
        for (String[] row : rows) {
            Lead lead = new Lead(row[0], row[1], row[2]); // Nom, Téléphone, Adresse
            leadsList.add(lead);
        }
        return leadsList;
    }

    private void sendSmsMessages(String messageTemplate) {
        // Récupérer la SIM locale (eSIM ou SIM physique)
        SubscriptionManager subscriptionManager = (SubscriptionManager) getSystemService(SUBSCRIPTION_SERVICE);
        List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            SubscriptionInfo subscriptionInfo = subscriptionInfoList.get(0); // Prendre la première SIM
            String phoneNumber = subscriptionInfo.getNumber();

            // Envoyer le message à chaque lead
            for (Lead lead : leads) {
                String message = messageTemplate.replace("{name}", lead.getName())
                        .replace("{phone}", lead.getPhone())
                        .replace("{address}", lead.getAddress());

                sendSmsWithDelay(lead.getPhone(), message, delayMs);
            }
        }
    }

    private void sendSmsWithDelay(String phoneNumber, String message, long delayMs) {
        // Envoyer le SMS avec un délai personnalisé
        new Handler().postDelayed(() -> {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(MainActivity.this, "Message envoyé à " + phoneNumber, Toast.LENGTH_SHORT).show();
        }, delayMs);
    }

    // Classe Lead pour stocker les informations des leads
    private static class Lead {
        private String name;
        private String phone;
        private String address;

        public Lead(String name, String phone, String address) {
            this.name = name;
            this.phone = phone;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getAddress() {
            return address;
        }
    }
}
