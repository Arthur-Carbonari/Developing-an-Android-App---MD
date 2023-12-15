package com.example.fitsync.auth

import android.content.Context
import com.example.fitsync.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Dagger Hilt module to provide authentication-related dependencies.
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    // Provides a singleton instance of FirebaseAuthRepository.
    @Singleton
    @Provides
    fun provideFirebaseAuthRepository(): FirebaseAuthRepository {
        return FirebaseAuthRepository()
    }

    // Provides GoogleSignInOptions configured for the app.
    @Provides
    @Singleton
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    // Provides a GoogleSignInClient configured with the given GoogleSignInOptions.
    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context, gso: GoogleSignInOptions): GoogleSignInClient {
        return GoogleSignIn.getClient(context, gso)
    }
}

