import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookingAppFx extends Application {
    private static final DateTimeFormatter DISPLAY_DATE_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private Stage stage;
    private List<Customer> customers = new ArrayList<>();
    private List<Barber> barbers = new ArrayList<>();
    private User currentUser;

    private TextField loginEmailField;
    private PasswordField loginPasswordField;
    private Label loginMessageLabel;

    private TextField customerNameField;
    private TextField customerEmailField;
    private PasswordField customerPasswordField;
    private TextField customerPostalCodeField;
    private Label customerMessageLabel;

    private TextField barberNameField;
    private TextField barberEmailField;
    private PasswordField barberPasswordField;
    private TextField barberSalonNameField;
    private TextField barberPostalCodeField;
    private Label barberMessageLabel;
    private ComboBox<Barber> bookingBarberBox;
    private ComboBox<Service> bookingServiceBox;
    private DatePicker bookingDatePicker;
    private TextField bookingTimeField;
    private Label bookingMessageLabel;
    private ListView<Booking> customerPendingBookingsList;
    private ListView<Booking> customerUpcomingBookingsList;
    private ListView<Booking> customerCancelledBookingsList;
    private TextField salonSearchField;
    private ListView<Barber> salonListView;
    private Label selectedSalonLabel;

    private TextField serviceNameField;
    private ComboBox<Category> serviceCategoryBox;
    private TextField servicePriceField;
    private TextField serviceDurationField;
    private Label serviceMessageLabel;
    private ListView<String> barberServicesList;
    private ListView<BookingView> barberBookingRequestsList;
    private ListView<BookingView> barberUpcomingBookingsList;
    private ListView<BookingView> barberCancelledBookingsList;
    private Label barberBookingMessageLabel;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Frisorbooking");
        stage.setMinWidth(760);
        stage.setMinHeight(520);

        loadUsers();
        showLoginView();
        stage.show();
    }

    private void loadUsers() {
        customers = FileHandler.readCustomersFromLogin();
        barbers = FileHandler.readBarbersFromLoginAndSalon();
    }

    private void showLoginView() {
        loginEmailField = new TextField();
        loginEmailField.setPromptText("Email");
        loginPasswordField = new PasswordField();
        loginPasswordField.setPromptText("Password");
        loginMessageLabel = createMessageLabel();

        GridPane form = createForm();
        addRow(form, 0, "Email", loginEmailField);
        addRow(form, 1, "Password", loginPasswordField);

        Button loginButton = new Button("Log in");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(event -> login());

        Button createCustomerButton = new Button("Create customer");
        createCustomerButton.setOnAction(event -> showCustomerRegisterView());

        Button createBarberButton = new Button("Create barber");
        createBarberButton.setOnAction(event -> showBarberRegisterView());

        VBox content = createPage("Login");
        content.getChildren().addAll(
                form,
                createButtonRow(loginButton, createCustomerButton, createBarberButton),
                loginMessageLabel
        );

        stage.setScene(createScene(content));
    }

    private void showCustomerRegisterView() {
        customerNameField = new TextField();
        customerEmailField = new TextField();
        customerPasswordField = new PasswordField();
        customerPostalCodeField = new TextField();
        customerMessageLabel = createMessageLabel();

        GridPane form = createForm();
        addRow(form, 0, "Name", customerNameField);
        addRow(form, 1, "Email", customerEmailField);
        addRow(form, 2, "Password", customerPasswordField);
        addRow(form, 3, "Postal code", customerPostalCodeField);

        Button createButton = new Button("Create customer");
        createButton.setDefaultButton(true);
        createButton.setOnAction(event -> registerCustomer());

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> showLoginView());

        VBox content = createPage("Create customer");
        content.getChildren().addAll(form, createButtonRow(createButton, backButton), customerMessageLabel);
        stage.setScene(createScene(content));
    }

    private void showBarberRegisterView() {
        barberNameField = new TextField();
        barberEmailField = new TextField();
        barberPasswordField = new PasswordField();
        barberSalonNameField = new TextField();
        barberPostalCodeField = new TextField();
        barberMessageLabel = createMessageLabel();

        GridPane form = createForm();
        addRow(form, 0, "Name", barberNameField);
        addRow(form, 1, "Email", barberEmailField);
        addRow(form, 2, "Password", barberPasswordField);
        addRow(form, 3, "Salon name", barberSalonNameField);
        addRow(form, 4, "Postal code", barberPostalCodeField);

        Button createButton = new Button("Create barber");
        createButton.setDefaultButton(true);
        createButton.setOnAction(event -> registerBarber());

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> showLoginView());

        VBox content = createPage("Create barber");
        content.getChildren().addAll(form, createButtonRow(createButton, backButton), barberMessageLabel);
        stage.setScene(createScene(content));
    }

    private void showCustomerDashboard() {
        loadUsers();

        salonSearchField = new TextField();
        salonSearchField.setPromptText("Search salon, barber or postal code");
        salonSearchField.textProperty().addListener((observable, oldValue, newValue) -> refreshSalonList());

        salonListView = new ListView<>();
        salonListView.setPrefHeight(170);
        salonListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectSalon(newValue)
        );

        selectedSalonLabel = new Label("Choose a salon to see services.");
        bookingServiceBox = new ComboBox<>();
        bookingServiceBox.setPrefWidth(360);
        bookingDatePicker = new DatePicker(LocalDate.now());
        bookingTimeField = new TextField("14:00");
        bookingMessageLabel = createMessageLabel();
        customerPendingBookingsList = new ListView<>();
        customerPendingBookingsList.setPrefHeight(90);
        customerUpcomingBookingsList = new ListView<>();
        customerUpcomingBookingsList.setPrefHeight(90);
        customerCancelledBookingsList = new ListView<>();
        customerCancelledBookingsList.setPrefHeight(80);

        GridPane bookingForm = createForm();
        addRow(bookingForm, 0, "Service", bookingServiceBox);
        addRow(bookingForm, 1, "Date", bookingDatePicker);
        addRow(bookingForm, 2, "Time (HH:mm)", bookingTimeField);

        Button bookButton = new Button("Book time");
        bookButton.setDefaultButton(true);
        bookButton.setOnAction(event -> bookTime());

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(event -> logout());

        Button cancelCustomerBookingButton = new Button("Cancel selected booking");
        cancelCustomerBookingButton.setOnAction(event -> cancelSelectedCustomerBooking());

        VBox content = createPage("Customer: " + currentUser.getName());
        content.getChildren().addAll(
                new Label("Find salons"),
                salonSearchField,
                salonListView,
                selectedSalonLabel,
                new Label("Book selected salon"),
                bookingForm,
                createButtonRow(bookButton, cancelCustomerBookingButton, logoutButton),
                bookingMessageLabel,
                new Label("Pending requests"),
                customerPendingBookingsList,
                new Label("Upcoming accepted bookings"),
                customerUpcomingBookingsList,
                new Label("Cancelled bookings"),
                customerCancelledBookingsList
        );
        refreshSalonList();
        refreshCustomerBookings();
        stage.setScene(createScene(content));
    }

    private void showBarberDashboard() {
        Barber barber = (Barber) currentUser;

        Label salonLabel = new Label("Salon: " + emptyFallback(barber.getSalonName()));
        Label postalCodeLabel = new Label("Postal code: " + emptyFallback(barber.getPostalCode()));

        serviceNameField = new TextField();
        serviceCategoryBox = new ComboBox<>();
        serviceCategoryBox.getItems().addAll(Category.values());
        serviceCategoryBox.setValue(Category.FADE);
        servicePriceField = new TextField();
        serviceDurationField = new TextField();
        serviceMessageLabel = createMessageLabel();
        barberServicesList = new ListView<>();
        barberServicesList.setPrefHeight(130);
        barberBookingRequestsList = new ListView<>();
        barberBookingRequestsList.setPrefHeight(100);
        barberUpcomingBookingsList = new ListView<>();
        barberUpcomingBookingsList.setPrefHeight(100);
        barberCancelledBookingsList = new ListView<>();
        barberCancelledBookingsList.setPrefHeight(80);
        barberBookingMessageLabel = createMessageLabel();

        GridPane serviceForm = createForm();
        addRow(serviceForm, 0, "Service name", serviceNameField);
        addRow(serviceForm, 1, "Category", serviceCategoryBox);
        addRow(serviceForm, 2, "Price", servicePriceField);
        addRow(serviceForm, 3, "Duration minutes", serviceDurationField);

        Button addServiceButton = new Button("Add service");
        addServiceButton.setDefaultButton(true);
        addServiceButton.setOnAction(event -> addService());

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(event -> logout());

        Button acceptBookingButton = new Button("Accept selected booking");
        acceptBookingButton.setOnAction(event -> acceptSelectedBooking());

        Button cancelBarberBookingButton = new Button("Cancel selected booking");
        cancelBarberBookingButton.setOnAction(event -> cancelSelectedBarberBooking());

        VBox content = createPage("Barber: " + barber.getName());
        content.getChildren().addAll(
                salonLabel,
                postalCodeLabel,
                new Label("Pending booking requests"),
                barberBookingRequestsList,
                new Label("Upcoming accepted bookings"),
                barberUpcomingBookingsList,
                new Label("Cancelled bookings"),
                barberCancelledBookingsList,
                createButtonRow(acceptBookingButton, cancelBarberBookingButton),
                barberBookingMessageLabel,
                new Label("Service management"),
                serviceForm,
                createButtonRow(addServiceButton, logoutButton),
                serviceMessageLabel,
                new Label("My services"),
                barberServicesList
        );
        refreshBarberBookingLists();
        refreshBarberServices();
        stage.setScene(createScene(content));
    }

    private void login() {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (hasEmptyValue(email, password)) {
            showMessage(loginMessageLabel, "Email and password are required.", true);
            return;
        }

        loadUsers();

        for (Customer customer : customers) {
            if (customer.getEmail().equalsIgnoreCase(email) && customer.login(password)) {
                currentUser = customer;
                showCustomerDashboard();
                return;
            }
        }

        for (Barber barber : barbers) {
            if (barber.getEmail().equalsIgnoreCase(email) && barber.login(password)) {
                currentUser = barber;
                showBarberDashboard();
                return;
            }
        }

        showMessage(loginMessageLabel, "Invalid login.", true);
    }

    private void registerCustomer() {
        String name = customerNameField.getText().trim();
        String email = customerEmailField.getText().trim();
        String password = customerPasswordField.getText().trim();
        String postalCode = customerPostalCodeField.getText().trim();

        if (hasEmptyValue(name, email, password, postalCode)) {
            showMessage(customerMessageLabel, "All fields are required.", true);
            return;
        }

        if (FileHandler.emailExists(email)) {
            showMessage(customerMessageLabel, "A user with that email already exists.", true);
            return;
        }

        Customer customer = Customer.register(name, email, password, postalCode);
        FileHandler.appendCustomerLogin(customer);
        customers.add(customer);
        showMessage(customerMessageLabel, "Customer created. You can log in now.", false);
        clearCustomerFields();
    }

    private void registerBarber() {
        String name = barberNameField.getText().trim();
        String email = barberEmailField.getText().trim();
        String password = barberPasswordField.getText().trim();
        String salonName = barberSalonNameField.getText().trim();
        String postalCode = barberPostalCodeField.getText().trim();

        if (hasEmptyValue(name, email, password, salonName, postalCode)) {
            showMessage(barberMessageLabel, "All fields are required.", true);
            return;
        }

        if (FileHandler.emailExists(email)) {
            showMessage(barberMessageLabel, "A user with that email already exists.", true);
            return;
        }

        Barber barber = Barber.register(name, email, password, salonName, postalCode);
        FileHandler.appendBarberLogin(barber);
        FileHandler.appendSalon(barber);
        barbers.add(barber);
        showMessage(barberMessageLabel, "Barber created. You can log in now.", false);
        clearBarberFields();
    }

    private void logout() {
        currentUser = null;
        showLoginView();
    }

    private void refreshBookingServices() {
        bookingServiceBox.getItems().clear();
        Barber selectedBarber = salonListView.getSelectionModel().getSelectedItem();

        if (selectedBarber != null) {
            bookingServiceBox.getItems().addAll(selectedBarber.getServices());
            if (!bookingServiceBox.getItems().isEmpty()) {
                bookingServiceBox.setValue(bookingServiceBox.getItems().get(0));
            }
        }
    }

    private void bookTime() {
        Barber barber = salonListView.getSelectionModel().getSelectedItem();
        Service service = bookingServiceBox.getValue();
        LocalDate date = bookingDatePicker.getValue();
        String timeText = bookingTimeField.getText().trim();

        if (barber == null || service == null || date == null || timeText.isEmpty()) {
            showMessage(bookingMessageLabel, "Choose barber, service, date and time.", true);
            return;
        }

        try {
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.parse(timeText));
            if (FileHandler.hasConfirmedBookingAt(barber.getId(), dateTime, barbers)) {
                showMessage(bookingMessageLabel, "That time is already accepted by the barber.", true);
                return;
            }

            Customer customer = (Customer) currentUser;
            FileHandler.createBooking(customer, barber, service, dateTime);
            showMessage(bookingMessageLabel, "Booking request sent. Waiting for barber approval.", false);
            refreshCustomerBookings();
        } catch (RuntimeException e) {
            showMessage(bookingMessageLabel, "Time must use HH:mm, for example 14:30.", true);
        }
    }

    private void refreshCustomerBookings() {
        customerPendingBookingsList.getItems().clear();
        customerUpcomingBookingsList.getItems().clear();
        customerCancelledBookingsList.getItems().clear();
        Customer customer = (Customer) currentUser;
        List<Booking> bookings = FileHandler.readBookingsForCustomer(customer.getId(), barbers);

        if (bookings.isEmpty()) {
            return;
        }

        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.PENDING) {
                customerPendingBookingsList.getItems().add(booking);
            } else if (booking.getStatus() == BookingStatus.CONFIRMED) {
                customerUpcomingBookingsList.getItems().add(booking);
            } else if (booking.getStatus() == BookingStatus.CANCELLED) {
                customerCancelledBookingsList.getItems().add(booking);
            }
        }
    }

    private void refreshSalonList() {
        salonListView.getItems().clear();
        String search = salonSearchField.getText().trim().toLowerCase();
        Customer customer = (Customer) currentUser;

        List<Barber> filteredBarbers = new ArrayList<>();
        for (Barber barber : barbers) {
            String searchableText = (
                    barber.getSalonName() + " " +
                            barber.getName() + " " +
                            barber.getPostalCode()
            ).toLowerCase();

            if (search.isEmpty() || searchableText.contains(search)) {
                filteredBarbers.add(barber);
            }
        }

        filteredBarbers.sort(Comparator.comparingInt(barber -> postalCodeDistance(
                customer.getPostalCode(),
                barber.getPostalCode()
        )));

        salonListView.getItems().addAll(filteredBarbers);

        if (!filteredBarbers.isEmpty()) {
            salonListView.getSelectionModel().select(0);
        } else {
            selectedSalonLabel.setText("No salons match your search.");
            bookingServiceBox.getItems().clear();
        }
    }

    private void selectSalon(Barber barber) {
        if (barber == null) {
            selectedSalonLabel.setText("Choose a salon to see services.");
            bookingServiceBox.getItems().clear();
            return;
        }

        Customer customer = (Customer) currentUser;
        int distance = postalCodeDistance(customer.getPostalCode(), barber.getPostalCode());
        selectedSalonLabel.setText(
                barber.getSalonName() + " - " +
                        barber.getName() + " - postal code " +
                        barber.getPostalCode() + " - distance score " +
                        distance
        );
        refreshBookingServices();
    }

    private int postalCodeDistance(String customerPostalCode, String barberPostalCode) {
        try {
            return Math.abs(Integer.parseInt(customerPostalCode) - Integer.parseInt(barberPostalCode));
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private void refreshBarberBookingLists() {
        barberBookingRequestsList.getItems().clear();
        barberUpcomingBookingsList.getItems().clear();
        barberCancelledBookingsList.getItems().clear();
        Barber barber = (Barber) currentUser;
        List<Booking> bookings = FileHandler.readBookingsForBarber(barber.getId(), barbers);

        for (Booking booking : bookings) {
            BookingView bookingView = new BookingView(booking, FileHandler.findCustomerById(booking.getCustomerId()));
            if (booking.getStatus() == BookingStatus.PENDING) {
                barberBookingRequestsList.getItems().add(bookingView);
            } else if (booking.getStatus() == BookingStatus.CONFIRMED) {
                barberUpcomingBookingsList.getItems().add(bookingView);
            } else if (booking.getStatus() == BookingStatus.CANCELLED) {
                barberCancelledBookingsList.getItems().add(bookingView);
            }
        }

        if (barberBookingRequestsList.getItems().isEmpty()) {
            showMessage(barberBookingMessageLabel, "No pending booking requests.", false);
        } else {
            barberBookingMessageLabel.setText(" ");
        }
    }

    private void acceptSelectedBooking() {
        BookingView selectedBookingView = barberBookingRequestsList.getSelectionModel().getSelectedItem();

        if (selectedBookingView == null) {
            showMessage(barberBookingMessageLabel, "Select a booking request first.", true);
            return;
        }

        Booking selectedBooking = selectedBookingView.getBooking();

        if (FileHandler.hasConfirmedBookingAt(
                selectedBooking.getBarber().getId(),
                selectedBooking.getDateTime(),
                barbers
        )) {
            showMessage(barberBookingMessageLabel, "That time is already accepted.", true);
            return;
        }

        FileHandler.confirmBooking(selectedBooking.getId(), barbers);
        showMessage(barberBookingMessageLabel, "Booking accepted.", false);
        refreshBarberBookingLists();
    }

    private void cancelSelectedCustomerBooking() {
        Booking selectedBooking = getSelectedCustomerBooking();

        if (selectedBooking == null) {
            showMessage(bookingMessageLabel, "Select a pending or accepted booking first.", true);
            return;
        }

        double fee = FileHandler.cancelBookingByCustomer(selectedBooking.getId(), barbers);
        if (fee > 0) {
            showMessage(bookingMessageLabel, "Booking cancelled. Same-day cancellation fee: " + fee + " kr.", true);
        } else {
            showMessage(bookingMessageLabel, "Booking cancelled.", false);
        }
        refreshCustomerBookings();
    }

    private Booking getSelectedCustomerBooking() {
        Booking selectedBooking = customerPendingBookingsList.getSelectionModel().getSelectedItem();
        if (selectedBooking != null) {
            return selectedBooking;
        }
        return customerUpcomingBookingsList.getSelectionModel().getSelectedItem();
    }

    private void cancelSelectedBarberBooking() {
        BookingView selectedBookingView = barberBookingRequestsList.getSelectionModel().getSelectedItem();
        if (selectedBookingView == null) {
            selectedBookingView = barberUpcomingBookingsList.getSelectionModel().getSelectedItem();
        }

        if (selectedBookingView == null) {
            showMessage(barberBookingMessageLabel, "Select a pending or accepted booking first.", true);
            return;
        }

        Booking selectedBooking = selectedBookingView.getBooking();
        FileHandler.cancelBookingByBarber(selectedBooking.getId(), barbers);
        showMessage(barberBookingMessageLabel, "Booking cancelled.", false);
        refreshBarberBookingLists();
    }

    private void addService() {
        String name = serviceNameField.getText().trim();
        Category category = serviceCategoryBox.getValue();
        String priceText = servicePriceField.getText().trim();
        String durationText = serviceDurationField.getText().trim();

        if (hasEmptyValue(name, priceText, durationText) || category == null) {
            showMessage(serviceMessageLabel, "All service fields are required.", true);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            int duration = Integer.parseInt(durationText);
            Barber barber = (Barber) currentUser;
            Service service = barber.addService(name, category, price, duration);
            FileHandler.appendService(barber, service);
            showMessage(serviceMessageLabel, "Service added.", false);
            clearServiceFields();
            refreshBarberServices();
        } catch (NumberFormatException e) {
            showMessage(serviceMessageLabel, "Price and duration must be numbers.", true);
        }
    }

    private void refreshBarberServices() {
        barberServicesList.getItems().clear();
        Barber barber = (Barber) currentUser;

        if (barber.getServices().isEmpty()) {
            barberServicesList.getItems().add("No services yet.");
            return;
        }

        for (Service service : barber.getServices()) {
            barberServicesList.getItems().add(formatService(service));
        }
    }

    private Scene createScene(VBox content) {
        applyReadableTheme(content);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: #eef1f5; -fx-background: #eef1f5;");

        BorderPane root = new BorderPane(scrollPane);
        root.setStyle("-fx-background-color: #eef1f5;");
        return new Scene(root, 900, 680);
    }

    private VBox createPage(String title) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(36, 64, 36, 64));
        content.setAlignment(Pos.TOP_LEFT);
        content.setMinWidth(720);
        content.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #111827;");
        content.getChildren().add(titleLabel);
        return content;
    }

    private GridPane createForm() {
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setMaxWidth(540);
        return form;
    }

    private void addRow(GridPane form, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #111827;");
        if (field instanceof TextField) {
            ((TextField) field).setPrefWidth(320);
            field.setStyle("-fx-text-fill: #111827; -fx-background-color: #ffffff; -fx-border-color: #cbd5e1;");
        } else if (field instanceof ComboBox) {
            field.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1;");
        } else if (field instanceof DatePicker) {
            field.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cbd5e1;");
        }
        form.add(label, 0, row);
        form.add(field, 1, row);
    }

    private HBox createButtonRow(Button... buttons) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(buttons);
        return row;
    }

    private Label createMessageLabel() {
        Label label = new Label(" ");
        label.setStyle("-fx-text-fill: #111827;");
        return label;
    }

    private void showMessage(Label label, String message, boolean error) {
        label.setStyle(error ? "-fx-text-fill: #9b1c1c;" : "-fx-text-fill: #166534;");
        label.setText(message);
    }

    private void applyReadableTheme(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getStyle() == null || !label.getStyle().contains("-fx-text-fill")) {
                    label.setStyle("-fx-text-fill: #111827;");
                }
            } else if (node instanceof Button) {
                node.setStyle("-fx-text-fill: #111827; -fx-background-color: #e5e7eb; -fx-border-color: #cbd5e1;");
            } else if (node instanceof ListView) {
                node.setStyle("-fx-control-inner-background: #ffffff; -fx-background-color: #ffffff; -fx-border-color: #cbd5e1; -fx-text-fill: #111827;");
            } else if (node instanceof TextField) {
                node.setStyle("-fx-text-fill: #111827; -fx-background-color: #ffffff; -fx-border-color: #cbd5e1;");
            }

            if (node instanceof Parent) {
                applyReadableTheme((Parent) node);
            }
        }
    }

    private boolean hasEmptyValue(String... values) {
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String emptyFallback(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not set";
        }
        return value;
    }

    private String formatService(Service service) {
        return service.getName() + " - " +
                service.getCategory() + " - " +
                service.getPrice() + " kr - " +
                service.getDurationMinutes() + " min";
    }

    private void clearCustomerFields() {
        customerNameField.clear();
        customerEmailField.clear();
        customerPasswordField.clear();
        customerPostalCodeField.clear();
    }

    private void clearBarberFields() {
        barberNameField.clear();
        barberEmailField.clear();
        barberPasswordField.clear();
        barberSalonNameField.clear();
        barberPostalCodeField.clear();
    }

    private void clearServiceFields() {
        serviceNameField.clear();
        servicePriceField.clear();
        serviceDurationField.clear();
    }
}
