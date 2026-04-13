package appli.ui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Optional;

/**
 * Utilitaire centralisé pour afficher des boites de dialogue JavaFX.
 * Simplifie les appels aux {@link javafx.scene.control.Alert} standards (information,
 * erreur, avertissement, confirmation) et offre des dialogues de saisie texte.
 */
public class AlertHelper {

    private AlertHelper() {}

    /** Affiche une boite de dialogue d'information et attend la fermeture. */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Affiche une boite de dialogue d'erreur et attend la fermeture. */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Affiche une boite de dialogue d'avertissement et attend la fermeture. */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boite de confirmation OK/Annuler.
     * @return {@code true} si l'utilisateur a clique sur OK
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Affiche un champ de saisie sur une ligne.
     * @return la valeur saisie, ou {@link java.util.Optional#empty()} si annule
     */
    public static Optional<String> showTextInput(String title, String header, String promptText) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(promptText);
        return dialog.showAndWait();
    }

    /**
     * Affiche une zone de saisie multiligne (TextArea).
     * @return le texte saisi, ou {@link java.util.Optional#empty()} si annule
     */
    public static Optional<String> showTextAreaInput(String title, String header) {
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);

        GridPane grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.add(textArea, 0, 0);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return textArea.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
