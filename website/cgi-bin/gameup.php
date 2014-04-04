<?php

if (isset($_POST['emailSubmit'])) {
    $file = fopen('../../Desktop/emaillist.txt','a+');
    $email = $_POST['email'];
    $fmail = $email.PHP_EOL;
    fwrite($file,$fmail);
    fclose($file); 
    print_r(error_get_last());
}

?>