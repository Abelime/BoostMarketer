function insertFetch(blogId) {
    const blogIds = [];
    $("input[name='blogId']").each(function () {
        blogIds.push($(this).val());
    });

    if (blogIds.includes(blogId)) {
        Swal.fire({
            icon: "error",
            title: "등록한 블로그 입니다"
        });
        $('#blogUrl').val("");
        return;
    }
    FunLoadingBarStart();

    fetch('/blog/' + blogId, {
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) {
                // 서버에서 응답 메시지(오류 메시지)를 JSON 형식으로 기대하는 경우
                return response.text().then(text => {
                    throw new Error(text || '서버 응답이 실패했습니다.');
                });
            }
            // 성공적인 응답 처리
            location.replace('/blog?pageSize=' + pageSize + '&sort=' + sort);
        })
        .catch(error => {
            // 오류 메시지 출력
            Swal.fire({
                icon: "error",
                text: error.message
            });
        })
        .finally(() => {
            FunLoadingBarEnd();
        });
}

function processBlogUrl() {
    var blogId = $('#blogUrl').val();
    var regex = /blog.naver.com\/([^\/]+)/;
    var match = blogId.match(regex);

    if (match && match[1]) {
        blogId = match[1];
        const blogList = blogId;
        insertFetch(blogList);
    } else {
        $('#blogUrl').val('');
    }
}

$('#insertBlogLink').click(function (event) {
    event.preventDefault();
    processBlogUrl();
});

$('#blogUrl').keypress(function (event) {
    if (event.which === 13) {
        event.preventDefault();
        processBlogUrl();
    }
});


function updateBlog(blogId) {
    FunLoadingBarStart();
    // Fetch API를 사용하여 요청 보내기
    fetch('/blog/' + blogId, {
        method: 'PUT',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('서버 응답이 실패했습니다.');
            }
            location.replace('/blog?pageSize=' + pageSize + '&sort=' + sort);
        })
        .catch(error => {
            console.error('오류가 발생했습니다:', error);
        })
        .finally(() => {
            FunLoadingBarEnd();
        });
}

function deleteBlog(blogId) {
    Swal.fire({
        title: "삭제하시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "삭제",
        cancelButtonText: '취소',
    }).then((result) => {
        if (result.isConfirmed) {
            // Fetch API를 사용하여 요청 보내기
            fetch('/blog/' + blogId, {
                method: 'DELETE',
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('서버 응답이 실패했습니다.');
                    }
                    location.replace('/blog?pageSize=' + pageSize + '&sort=' + sort);
                })
                .catch(error => {
                    console.error('오류가 발생했습니다:', error);
                });
        }
    })
}

function openPopUp(blogId) {
    var url = "/blog/popup/" + blogId;
    var name = "blog_popup";
    var option = "width = 1100, height = 500, top = 100, left = 200, location = no"
    window.open(url, name, option);
}

function missingPostPopUp(blogId) {
    var url = "/blog/popup/missing/" + blogId;
    var name = "blog_popup";
    var option = "width = 1100, height = 500, top = 100, left = 200, location = no"
    window.open(url, name, option);
}

window.changePage = function (page) {
    window.location.href = '/blog?page=' + page + '&pageSize=' + pageSize + '&sort=' + sort;
}