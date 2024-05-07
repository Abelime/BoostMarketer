// 전역 변수로 검증 상태 저장
let isEmailDuplicated = true; // 초기값은 중복으로 가정
let isPasswordMatched = false;

function doGoogleLogin() {
  fetch('/user/api/v1/oauth2/google', {
    method: 'POST',
  })
          .then(response => response.text()) // 서버로부터 URL을 텍스트로 받아옴
          .then(url => {
            window.location.href = url; // 받아온 URL로 리다이렉트
          })
          .catch(error => console.error('Error:', error));
}

function clickSignupBtn(){
  if (!isEmailDuplicated && isPasswordMatched) {
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    var checkPassword = document.getElementById('checkPassword').value;

    // 이메일 및 비밀번호 not null 확인
    if (!email || !password || !checkPassword) {
      alert('이메일과 비밀번호를 입력하세요.');
      return;
    }

    // 이메일 형식 확인
    var emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      alert('올바른 이메일 주소를 입력하세요.');
      return;
    }

    // 비밀번호 일치 확인
    if (password !== checkPassword) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    // 컨트롤러 호출
    fetch('/user/sign-up', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email,
        password: password
      })
    })
            .then(response => {
              if (!response.ok) {
                return response.text().then(data => {
                  alert(data); // 서버로부터 받은 에러 메시지 출력
                  throw new Error('회원가입 실패');
                });
              }
              return response.text(); // 응답을 텍스트 형식으로 파싱
            })
            .then(data => {
              alert(data); // 성공 응답 처리
              window.location = '/user/sign-in'; // 페이지 리디렉션
            })
            .catch(error => {
              console.error('Error:', error); // 오류 처리
            });
  }
}

function checkEmailDuplication() {
  const email = document.getElementById('email');
  const errorMessage = document.getElementById('email-error-message');

  if(email.value === ''){
    email.style.borderColor = ''; // Reset to default or a specific color if you prefer
    errorMessage.textContent = ''; // Clear any previous error message
    return;
  }

  fetch('/user/checkDuplication/' + email.value)
          .then(response => {
            if (!response.ok) {
              throw new Error('Server error');
            }
            return response.json(); // Assuming the response is JSON. Use response.text() if it's text.
          })
          .then(data => {
            if(data === true){
              // If email is a duplicate
              isEmailDuplicated = true;
              email.style.borderColor = 'red';
              errorMessage.textContent = '사용할 수 없는 아이디입니다. 다른 아이디를 입력해 주세요.';
            } else {
              isEmailDuplicated = false;
              // If email is not a duplicate
              email.style.borderColor = ''; // Reset to default or a specific color if you prefer
              errorMessage.textContent = ''; // Clear any previous error message
            }
          })
          .catch(error => {
            console.error('Error:', error);
          });
}

function passwordCheck() {
  const password = document.getElementById('password').value;
  const checkPassword = document.getElementById('checkPassword').value;
  const passwordErrorMessage = document.getElementById('password-error-message');

  if(password !== checkPassword){
    // If passwords do not match
    passwordErrorMessage.textContent = '비밀번호가 일치하지 않습니다.';
    isPasswordMatched = false;
  } else {
    // If passwords match
    passwordErrorMessage.textContent = ''; // Clear any previous error message
    isPasswordMatched = true;
  }
}