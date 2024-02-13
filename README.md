# KEYS Password Manager
Kotlin Externship Team Project

## Description

Welcome to the Kotlin Externship Team Project. This is a robust and modern application, designed and built as part of an externship program. Our team is using latest Jetpack Compose, using Kotlin programming language.

The application is an exciting project that serves users in managing a unique entity known as "expanse". This could be viewed as a learning project to explore modern Android development techniques, libraries, and best practices.


## Features

### Chats

### Notes Management

The core functionality of the application revolves around managing user login credientials. Users have the ability to add new password, edit existing ones, and delete any they no longer need. To atuometically fill in login page.

### Password Management

The core functionality of the application revolves around managing user login credientials. Users have the ability to add new password, edit existing ones, and delete any they no longer need. To atuometically fill in login page.


## Project Structure

Our project is organized into several packages to ensure a clean architecture(used MVVM architecture):

### Auth

The application offers a system for user authentication. It provides users with the ability to sign in using their Google account. The authentication process is streamlined to provide a seamless user experience.

### Access verification

Once users are authenticated, they have to ask permission to access account. So, that we prevent access data by an unauthorize person.

### Autofill service

### Chats

### Core

This package contains the base code used throughout the application. `Constants.kt` is where you'll find all constants used in the application, including those for authentication, screen names, and messaging. The `Utils.kt` file is home to utility functions, including a function for logging exceptions which aids greatly in debugging and development.

### di

This package contains the model `AppModel.kt` dependency injection

### navigation

### Notes

### Notification Service

### On-boarding Screens

### Password

### presentation

This package contains UI logic. Include 'MainActivity'.

### Setting

## Upcoming Features

As the application is still under active development, we are continually working on adding more features and improving user experience. We have exciting plans for future updates and we will keep the community updated as we progress.

### Feature 1

Improve Autofill Service.

### Feature 2

Imptove Notification management.


## Acknowledgments

Thank you for your interest in the KEYS Password Manager Project!
