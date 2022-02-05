import hashlib
import os.path
import socket
from getpass import getpass
import tqdm

from file_dto import *


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
        send_str_with_header(inp)
        if auth_method == "register":
            inp = input("Email: ")
            send_str_with_header(inp)
        inp = getpass("Password: ")
        send_str_with_header(inp)
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


def send_str_with_header(message, header_size=4):
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


def read_int(size=4):
    return int.from_bytes(sock.recv(size), byteorder='big')


def read_str_with_header():
    ln = int.from_bytes(sock.recv(4), byteorder='big')
    return sock.recv(ln).decode(encoding="UTF-8")


def read_response_code():
    try:
        code = read_int()
        if code >= 200:
            print("Exception message:", read_str_with_header() )
        return code
    except InterruptedError:
        return -1


def create_folder(path):
    print("Creating remote folder...", path)
    send_int_header(20)
    send_str_with_header(path)
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
    send_str_with_header(remote_path)
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
            if len(t) < 3:
                print("Format:upload -local_path -remote_path")
                continue
            upload_file(t[1], t[2])
        elif inp.startswith("rm "):
            t = inp.split()
            if len(t) < 2:
                print("Format:rm -remote_path")
                continue
            delete_remote_file(t[1])
        elif inp.startswith("ls "):
            t = inp.split()
            if len(t) < 2:
                print("Format:ls -remote_path")
                continue
            list_remote_files(t[1])
        elif inp.startswith("download "):
            t = inp.split()
            if len(t) < 3:
                print("Format: download -remote_path local_path")
                continue
            download_file(t[1], t[2])


def delete_remote_file(path):
    print("Deleting remote file: ", path)
    send_int_header(22)
    send_str_with_header(path)
    r = read_response_code()
    if r == 112:
        print(r, "successfully deleted file")
    else:
        print(r)


def read_file_dto():
    is_dir = sock.recv(1) == b'\x01'
    completed = sock.recv(1) == b'\x01'
    name = read_str_with_header()
    if not is_dir:
        sz = int.from_bytes(sock.recv(8), byteorder='big')
        dto = FileDto()
        dto.name = name
        dto.completed = completed
        dto.size = sz
        return dto
    dto = FileElem()
    dto.name = name
    dto.completed = completed
    return  dto


def list_remote_files(path):
    print("Requesting remote files: ", path)
    send_int_header(23)
    send_str_with_header(path)
    r = read_response_code()
    if r == 113:
        print(r)
        t = read_int()
        f = []
        for i in range(t):
            f.append(read_file_dto())
        for i in f:
            print(i.__str__())


def download_file(remote_path, local_path):
    if os.path.exists(local_path):
        print("File %s exists"%local_path)
        return
    send_int_header(24)
    send_str_with_header(remote_path)
    r = read_response_code()
    if r != 114:
        print("Unexpected response code ", r)
        return
    with open(local_path, "xb") as f:
        bytes_count = read_int(8)
        blocks = bytes_count//FILE_BUF_SIZE
        progress = tqdm.tqdm(range(bytes_count), f'Receiving  {bytes_count} bytes',unit_scale=True, unit_divisor=1024)
        md5_hash = hashlib.md5()
        bt_read = 0
        for i in range(blocks):
            recv = sock.recv(FILE_BUF_SIZE)
            f.write(recv)
            bt_read += FILE_BUF_SIZE
            md5_hash.update(recv)
            progress.update(len(recv))
            progress.refresh()
        if bt_read < bytes_count:
            recv = sock.recv(bytes_count-bt_read)
            f.write(recv)
            md5_hash.update(recv)
            progress.update(len(recv))
            progress.refresh()
        progress.close()
    print("File downloaded successfully")


FILE_BUF_SIZE = 4096

if __name__ == "__main__":
    print("Starting cloud-storage client..")
    sock = socket.socket()
    sock.connect(('localhost', 9096))
    print("Connected to server")
    auth()
    main_loop()

