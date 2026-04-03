package co.edu.unipiloto.loginsignupforms;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class signup_form extends AppCompatActivity {

    TextInputEditText etFullName, etUsername, etEmail,
            etAddress, etPassword, etConfirmPassword, etFecha;
    Spinner spinnerRol;
    RadioGroup radioGroupGenero;
    TextView tvLocation;
    FusedLocationProviderClient fusedLocationClient;

    int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        // Referencias a vistas
        etFullName        = findViewById(R.id.etFullName);
        etUsername        = findViewById(R.id.etUsername);
        etEmail           = findViewById(R.id.etEmail);
        etAddress         = findViewById(R.id.etAddress);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFecha           = findViewById(R.id.etFecha);
        spinnerRol        = findViewById(R.id.spinnerRol);
        radioGroupGenero  = findViewById(R.id.radioGroupGenero);
        tvLocation        = findViewById(R.id.tvLocation);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // DatePickerDialog al tocar el campo de fecha
        etFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selectedYear  = year;
                        selectedMonth = month;
                        selectedDay   = day;
                        etFecha.setText(day + "/" + (month + 1) + "/" + year);
                    },
                    cal.get(Calendar.YEAR) - 20,
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        findViewById(R.id.btnGetLocation).setOnClickListener(v -> getLocation());
        findViewById(R.id.btnRegister).setOnClickListener(v -> validateAndRegister());
    }

    private boolean isOver18() {
        if (etFecha.getText().toString().isEmpty()) return false;
        Calendar dob   = Calendar.getInstance();
        dob.set(selectedYear, selectedMonth, selectedDay);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--;
        return age >= 18;
    }

    private void validateAndRegister() {
        String name    = etFullName.getText().toString().trim();
        String user    = etUsername.getText().toString().trim();
        String email   = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String pass    = etPassword.getText().toString().trim();
        String pass2   = etConfirmPassword.getText().toString().trim();
        String fecha   = etFecha.getText().toString().trim();

        // Campos vacíos
        if (name.isEmpty() || user.isEmpty() || email.isEmpty()
                || address.isEmpty() || pass.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(this,
                    "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Passwords coinciden
        if (!pass.equals(pass2)) {
            Toast.makeText(this,
                    "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Rol seleccionado
        if (spinnerRol.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Selecciona un rol.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Género seleccionado
        if (radioGroupGenero.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this,
                    "Selecciona un género.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Edad mínima 18 años
        if (!isOver18()) {
            Toast.makeText(this,
                    "Debes ser mayor de 18 años.", Toast.LENGTH_LONG).show();
            return;
        }

        // Todo válido — aquí iría el registro real
        Toast.makeText(this,
                "Usuario registrado exitosamente!", Toast.LENGTH_SHORT).show();
        finish(); // Regresa al Login
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Primero intenta getLastLocation
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        tvLocation.setText(
                                "Lat: " + location.getLatitude()
                                        + "   Lng: " + location.getLongitude());
                    } else {
                        // Si es null, solicita ubicación activa
                        solicitarUbicacionActiva();
                    }
                });
    }

    private void solicitarUbicacionActiva() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdates(1)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result != null && result.getLastLocation() != null) {
                    Location loc = result.getLastLocation();
                    tvLocation.setText(
                            "Lat: " + loc.getLatitude()
                                    + "   Lng: " + loc.getLongitude());
                } else {
                    tvLocation.setText("No se pudo obtener ubicación.");
                }
                fusedLocationClient.removeLocationUpdates(this);
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    getMainLooper());
            tvLocation.setText("Buscando ubicación...");
        }
    }
}