package com.example.smartstock.controllers;

import com.example.smartstock.dao.CategoryDAO;
import com.example.smartstock.models.Category;
import com.example.smartstock.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.List;

public class CategoryController {
    private BorderPane mainLayout;
    private CategoryDAO categoryDAO;
    private TableView<Category> categoryTable;
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    
    public CategoryController(BorderPane mainLayout, CategoryDAO categoryDAO) {
        this.mainLayout = mainLayout;
        this.categoryDAO = categoryDAO;
    }
    
    public void show() {
        VBox view = new VBox(15);
        view.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Categories");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("Add Category");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        addBtn.setOnAction(e -> showAddDialog());
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editSelected());
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelected());
        
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn);
        
        categoryTable = new TableView<>();
        categoryTable.setItems(categoryList);
        
        TableColumn<Category, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        
        TableColumn<Category, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);
        
        TableColumn<Category, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(400);
        
        categoryTable.getColumns().addAll(idCol, nameCol, descCol);
        
        view.getChildren().addAll(titleLabel, toolbar, categoryTable);
        VBox.setVgrow(categoryTable, Priority.ALWAYS);
        
        mainLayout.setCenter(view);
        loadCategories();
    }
    
    private void loadCategories() {
        try {
            List<Category> categories = categoryDAO.findAll();
            categoryList.clear();
            categoryList.addAll(categories);
        } catch (SQLException e) {
            AlertUtil.showError("Database Error", "Failed to load categories: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        Dialog<Category> dialog = createFormDialog(null);
        dialog.showAndWait().ifPresent(category -> {
            try {
                categoryDAO.save(category);
                loadCategories();
                AlertUtil.showInfo("Success", "Category added successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to add category: " + e.getMessage());
            }
        });
    }
    
    private void editSelected() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a category to edit.");
            return;
        }
        
        Dialog<Category> dialog = createFormDialog(selected);
        dialog.showAndWait().ifPresent(category -> {
            try {
                categoryDAO.update(category);
                loadCategories();
                AlertUtil.showInfo("Success", "Category updated successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to update category: " + e.getMessage());
            }
        });
    }
    
    private void deleteSelected() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select a category to delete.");
            return;
        }
        
        if (AlertUtil.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete " + selected.getName() + "?")) {
            try {
                categoryDAO.delete(selected.getCategoryId());
                loadCategories();
                AlertUtil.showInfo("Success", "Category deleted successfully!");
            } catch (SQLException e) {
                AlertUtil.showError("Database Error", "Failed to delete category: " + e.getMessage());
            }
        }
    }
    
    private Dialog<Category> createFormDialog(Category category) {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle(category == null ? "Add Category" : "Edit Category");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        TextArea descField = new TextArea();
        descField.setPrefRowCount(3);
        
        if (category != null) {
            nameField.setText(category.getName());
            descField.setText(category.getDescription());
        }
        
        grid.add(new Label("Name:*"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Category result = category != null ? category : new Category();
                result.setName(nameField.getText().trim());
                result.setDescription(descField.getText().trim());
                return result;
            }
            return null;
        });
        
        return dialog;
    }
}