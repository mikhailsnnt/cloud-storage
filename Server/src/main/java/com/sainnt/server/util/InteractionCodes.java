package com.sainnt.server.util;

import com.sainnt.server.dto.LoginResult;
import com.sainnt.server.dto.RegistrationResult;
import com.sainnt.server.exception.*;
import com.sainnt.server.handler.RequestBuilder;
import com.sainnt.server.handler.req_builder.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InteractionCodes {
    private InteractionCodes() {
    }

    public static final int HEADER_SIZE = 4;
    public static final int CODE_EXIT = 3;
    public static final int CODE_LOGIN = 5;
    public static final int CODE_REGISTER = 6;
    public static final int CODE_INVALID_REQUEST = 7;
    public static final int CODE_START_UPLOAD = 8;
    public static final int CODE_UPLOADED_SUCCESSFULLY = 9;
    public static final int CODE_OP_CREATED_DIR = 110;
    public static final int CODE_OP_UPLOADED_FILE = 111;
    public static final int CODE_OP_DELETED_FILE = 112;
    public static final int CODE_OP_LIST_FILES = 113;
    public static final int CODE_OP_START_DOWNLOAD = 114;

    private static final Map<Integer, Class<? extends RequestBuilder>> REQUEST_CODES;
    private static final Map<Class<? extends ClientAvailableException>, Integer> EXCEPTION_CODES;
    private static final Map<RegistrationResult, Integer> REGISTRATION_RESULT_CODES;
    private static final Map<LoginResult.Result, Integer> LOGIN_RESULT_CODES;

    static {
        REQUEST_CODES = new HashMap<>();
        REQUEST_CODES.put(20, CreateDirectoryRequestBuilder.class);
        REQUEST_CODES.put(21, UploadFileRequestBuilder.class);
        REQUEST_CODES.put(22, DeleteFileRequestBuilder.class);
        REQUEST_CODES.put(23, FilesListRequestBuilder.class);
        REQUEST_CODES.put(24, DownloadFileRequestBuilder.class);

        REGISTRATION_RESULT_CODES = new HashMap<>();
        REGISTRATION_RESULT_CODES.put(RegistrationResult.success, 50);
        REGISTRATION_RESULT_CODES.put(RegistrationResult.email_invalid, 51);
        REGISTRATION_RESULT_CODES.put(RegistrationResult.password_invalid, 52);
        REGISTRATION_RESULT_CODES.put(RegistrationResult.username_occupied, 53);
        REGISTRATION_RESULT_CODES.put(RegistrationResult.email_exists, 54);
        REGISTRATION_RESULT_CODES.put(RegistrationResult.registration_failed, 55);

        LOGIN_RESULT_CODES = new HashMap<>();
        LOGIN_RESULT_CODES.put(LoginResult.Result.success, 100);
        LOGIN_RESULT_CODES.put(LoginResult.Result.bad_credentials, 101);
        LOGIN_RESULT_CODES.put(LoginResult.Result.user_already_logged_in, 102);

        EXCEPTION_CODES = new HashMap<>();
        EXCEPTION_CODES.put(AccessDeniedException.class, 200);
        EXCEPTION_CODES.put(CheckSumMismatchException.class, 201);
        EXCEPTION_CODES.put(DirectoryAlreadyExists.class, 202);
        EXCEPTION_CODES.put(DirectoryNotFoundException.class, 203);
        EXCEPTION_CODES.put(FileAlreadyExistsException.class, 204);
        EXCEPTION_CODES.put(FileNotFoundException.class, 205);
        EXCEPTION_CODES.put(InternalServerError.class, 206);
        EXCEPTION_CODES.put(InvalidFileNameException.class, 207);
    }

    public static Integer getExceptionCode(Class<? extends ClientAvailableException> e) {
        return EXCEPTION_CODES.get(e);
    }

    public static Optional<Class<? extends RequestBuilder>> getRequestBuilderClass(int code) {
        return Optional.ofNullable(REQUEST_CODES.get(code));
    }

    public static int getRegistrationResultCode(RegistrationResult result) {
        return REGISTRATION_RESULT_CODES.get(result);
    }

    public static int getLoginResultCode(LoginResult.Result result) {
        return LOGIN_RESULT_CODES.get(result);
    }
}
