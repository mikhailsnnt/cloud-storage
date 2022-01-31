import socket
from getpass import getpass


def auth():
    while True:
        auth_method = "login"
        while True:
            inp = input("Select authentication method: register(reg) or login(log)?")
            if inp in ("reg", "register"):
                auth_method = "register"
                sock.send(b'\1')
                break
            elif inp in ("login", "log"):
                sock.send(b'\0')
                break
        inp = input("Login: ")
        send_str_with_header(inp, 4)
        if auth_method == "register":
            inp = input("Email: ")
            send_str_with_header(inp,4)
        inp = getpass("Password: ")
        send_str_with_header(inp, 4)
        response = int.from_bytes(sock.recv(4), byteorder='big')
        if auth_method == "register":
            handle_registration_response(response)
        elif auth_method == "login":
            if handle_login_response(response):
                break


def handle_registration_response(response):
    if response == 50:
        print("Registration completed, please sign in")
        return True
    elif response == 51:
        print("Email is invalid")
    elif response == 52:
        print("Password is too short")
    elif response == 53:
        print("Username is occupied")
    elif response == 54:
        print("Email is already in use")
    elif response == 55:
        print("Internal server error, try again later")
    return False


def handle_login_response(response):
    if response == 100:
        print("Logged in successfully")
        return True
    elif response == 101:
        print("Bad credentials")
    elif response == 102:
        print("User is already logged in")
    return False


def send_str_with_header(message, header_size):
    sock.sendall(len(message).to_bytes(header_size, byteorder='big'))
    sock.sendall(message.encode())


def main_loop():
    while True:
        inp = input("Send message")
        sock.sendall(inp.encode())


if __name__ == "__main__":
    print("Starting cloud-storage client..")
    sock = socket.socket()
    sock.connect(('localhost',9096))
    print("Connected to server")
    auth()
    main_loop()

