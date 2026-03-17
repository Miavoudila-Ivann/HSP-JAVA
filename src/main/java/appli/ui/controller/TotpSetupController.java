package appli.ui.controller;

import appli.model.User;
import appli.security.TotpService;
import appli.service.AuthService;
import appli.security.SessionManager;
import appli.util.Route;
import appli.util.Router;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

public class TotpSetupController {

    @FXML private ImageView qrCodeView;
    @FXML private Label secretLabel;
    @FXML private Label statusLabel;
    @FXML private Label errorLabel;
    @FXML private TextField confirmCodeField;
    @FXML private Button btnEnable;
    @FXML private Button btnDisable;

    private final AuthService authService = new AuthService();
    private String generatedSecret;
    private User currentUser;

    @FXML
    public void initialize() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Router.goTo(Route.LOGIN);
            return;
        }

        if (currentUser.isTotpEnabled()) {
            // 2FA deja active : mode desactivation
            btnEnable.setVisible(false);
            btnEnable.setManaged(false);
            confirmCodeField.setVisible(false);
            confirmCodeField.setManaged(false);
            secretLabel.setText("2FA actuellement ACTIVE");
            qrCodeView.setVisible(false);
            qrCodeView.setManaged(false);
        } else {
            // 2FA non active : generer une nouvelle cle et afficher le QR
            btnDisable.setVisible(false);
            btnDisable.setManaged(false);
            generatedSecret = TotpService.generateSecret();
            secretLabel.setText(generatedSecret);
            displayQrCode(currentUser.getEmail(), generatedSecret);
        }
    }

    @FXML
    private void handleEnable() {
        String code = confirmCodeField.getText().trim();
        if (code.isEmpty()) {
            showError("Saisissez le code affiche dans votre application.");
            return;
        }

        if (authService.enableTotp(currentUser, generatedSecret, code)) {
            showStatus("2FA active avec succes !");
            btnEnable.setDisable(true);
            confirmCodeField.setDisable(true);
        } else {
            showError("Code incorrect. Reessayez en verifiant l'heure de votre appareil.");
            confirmCodeField.clear();
        }
    }

    @FXML
    private void handleDisable() {
        authService.disableTotp(currentUser);
        showStatus("2FA desactive.");
        btnDisable.setDisable(true);
    }

    @FXML
    private void handleBack() {
        Router.goTo(Route.DASHBOARD);
    }

    // --- QR code ---

    private void displayQrCode(String email, String secret) {
        try {
            String uri = TotpService.getOtpAuthUri(secret, email);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(uri, BarcodeFormat.QR_CODE, 220, 220);
            BufferedImage buffered = MatrixToImageWriter.toBufferedImage(matrix);
            Image fxImage = SwingFXUtils.toFXImage(buffered, null);
            qrCodeView.setImage(fxImage);
        } catch (WriterException e) {
            secretLabel.setText("Erreur QR: entrez la cle manuellement : " + secret);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
