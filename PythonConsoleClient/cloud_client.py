import hashlib
import os.path
import socket
from getpass import getpass
import tqdm


def auth():
    while True:
        auth_method = "login"
        while True:
            inp = input("Select authentication method: register(reg) or login(log)?")
            if inp in ("reg", "register"):
                auth_method = "register"
                send_int_header(6)
                break
            elif inp in ("login", "log"):
                send_int_header(5)
                break
        inp = input("Login: ")
        send_str_with_header(inp, 4)
        if auth_method == "register":
            inp = input("Email: ")
            send_str_with_header(inp,4)
        inp = getpass("Password: ")
        send_str_with_header(inp, 4)
        response = read_response_code()
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
        print("Registration failed")
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


def send_int_header(header, header_size=4):
    sock.sendall(header.to_bytes(header_size, byteorder='big'))


def send_file(path):
    filesize = os.path.getsize(path)
    filename = os.path.basename(path)
    md5_hash = hashlib.md5()
    a_file = open(path, "rb")
    content = a_file.read()
    md5_hash.update(content)
    send_int_header(filesize, 8)
    send_int_header(md5_hash.digest_size,4)
    sock.sendall(md5_hash.digest())
    r = read_response_code()
    print(r)
    if r != 8:
        return False
    progress = tqdm.tqdm(range(filesize), f'Sendinig {filename}',unit_scale=True, unit_divisor=1024)
    with open(path, "rb") as f:
        while True:
            byte_buf = f.read(FILE_BUF_SIZE)
            if not byte_buf:
                break
            sock.sendall(byte_buf)
            progress.update(len(byte_buf))
            progress.refresh()
    progress.close()
    r = read_response_code()
    if r == 9:
        return True
    print(r)
    return False


def read_str_with_header():
    ln = int.from_bytes(sock.recv(4), byteorder='big')
    return sock.recv(ln).decode(encoding="UTF-8")


def read_response_code():
    try:
        code = int.from_bytes(sock.recv(4), byteorder='big')
        if code >= 200:
            print("Exception message:", read_str_with_header() )
        return code
    except InterruptedError:
        return -1


def create_folder(path):
    print("Creating remote folder...", path)
    send_int_header(20)
    send_str_with_header(path,4)
    res = read_response_code()
    print("Return code: ", res)


def upload_file(local_path, remote_path):
    print("Uploading %s -> %s" % (local_path, remote_path))
    if not os.path.exists(local_path):
        print("Local path is incorrect!")
        return
    if os.path.isdir(local_path):
        print("Directory provided")
        return
    if remote_path[-1] == "/":
        remote_path += os.path.basename(local_path)
    send_int_header(21)
    send_str_with_header(remote_path, 4)
    if send_file(local_path):
        print("Successfully uploaded %s"%os.path.basename(local_path))


def main_loop():
    while True:
        inp = input(">")
        print(inp)
        if inp.startswith("mkdir "):
            create_folder(inp.split()[1])
        elif inp.startswith("upload "):
            t = inp.split()
            if len(t) < 2:
                print("Argument format: -local_path -remote_path")
                continue
            upload_file(t[1],t[2])


FILE_BUF_SIZE = 4096


if __name__ == "__main__":
    print("Starting cloud-storage client..")
    sock = socket.socket()
    sock.connect(('localhost',9096))
    print("Connected to server")
    auth()
    main_loop()

