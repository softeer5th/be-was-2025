document.addEventListener('DOMContentLoaded', () => {
    fetch('/user/info', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(response => response.text())
        .then(data => {
            const loginButton = document.querySelector('.header__menu__item a[href="/login/index.html"]');
            const registerButton = document.querySelector('.header__menu__item a[href="/registration/index.html"]');

            // 응답에서 userName 값을 추출
            const params = new URLSearchParams(data);
            const userName = params.get('userName');

            if (userName && userName !== 'null') {
                loginButton.textContent = userName;
                loginButton.href = '/mypage/index.html'; // 프로필 페이지로 링크 변경

                // "회원가입" 버튼을 제거하고 로그아웃 버튼 추가
                if (registerButton) {
                    const form = document.createElement('form');
                    form.action = '/user/logout';
                    form.method = 'POST';
                    form.style.display = 'inline';

                    const logoutButton = document.createElement('button');
                    logoutButton.type = 'submit';
                    logoutButton.id = 'logout-btn';
                    logoutButton.className = 'btn btn_ghost btn_size_s';
                    logoutButton.textContent = '로그아웃';

                    form.appendChild(logoutButton);
                    registerButton.parentNode.replaceChild(form, registerButton);
                }
            } else {
                loginButton.textContent = '로그인';
            }
        })
        .catch(error => {
            console.error('Error fetching user info:', error);
        });
});
