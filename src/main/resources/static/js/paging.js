document.addEventListener('DOMContentLoaded', function () {
    var totalPages = Math.ceil(totalCount / pageSize);
    var groupSize = 5; // 한 번에 표시할 페이지 번호의 개수

    // 페이지 그룹의 시작과 끝 페이지 번호 계산
    var startPage = Math.floor((page - 1) / groupSize) * groupSize + 1;
    var endPage = Math.min(startPage + groupSize - 1, totalPages);

    function createPagination(currentPage, totalPages, startPage, endPage) {
        var paginationHTML = '<ul class="pagination pagination-secondary justify-content-center">';

        // 처음으로 이동
        paginationHTML += '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:changePage(1);" aria-label="First">' +
            '<i class="fa fa-angle-double-left" aria-hidden="true"></i></a></li>';

        // 이전 페이지로 이동
        var prevPage = currentPage - 1;
        paginationHTML += '<li class="page-item ' + (currentPage === 1 ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:changePage(' + prevPage + ');" aria-label="Previous">' +
            '<i class="fa fa-angle-left" aria-hidden="true"></i></a></li>';

        // 페이지 번호 버튼
        for (var i = startPage; i <= endPage; i++) {
            if (i === currentPage) {
                // 현재 페이지에 대해 클릭 불가능한 상태로 표시
                paginationHTML += '<li class="page-item active"><span class="page-link">' + i + '</span></li>';
            } else {
                paginationHTML += '<li class="page-item">' +
                    '<a class="page-link" href="javascript:changePage(' + i + ');">' + i + '</a></li>';
            }
        }

        // 다음 페이지로 이동
        var nextPage = currentPage + 1;
        paginationHTML += '<li class="page-item ' + (currentPage === totalPages ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:changePage(' + nextPage + ');" aria-label="Next">' +
            '<i class="fa fa-angle-right" aria-hidden="true"></i></a></li>';

        // 마지막으로 이동
        paginationHTML += '<li class="page-item ' + (currentPage === totalPages ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:changePage(' + totalPages + ');" aria-label="Last">' +
            '<i class="fa fa-angle-double-right" aria-hidden="true"></i></a></li>';

        paginationHTML += '</ul>';

        document.getElementById('pagination').innerHTML = paginationHTML;
    }

    createPagination(page, totalPages, startPage, endPage);
});